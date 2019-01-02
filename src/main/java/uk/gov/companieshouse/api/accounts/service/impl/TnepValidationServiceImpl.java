package uk.gov.companieshouse.api.accounts.service.impl;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class TnepValidationServiceImpl implements TnepValidationService {

    private static final String IXBRL_VALIDATOR_URI = "IXBRL_VALIDATOR_URI";

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private RestTemplate restTemplate;
    private EnvironmentReader environmentReader;

    @Autowired
    public TnepValidationServiceImpl(RestTemplate restTemplate,
        EnvironmentReader environmentReader) {

        this.restTemplate = restTemplate;
        this.environmentReader = environmentReader;
    }

    /**
     * Validate the ixbrl
     *
     * @return boolean
     */
    @Override
    public boolean validate(String ixbrl, String location) {

        try {
            Results results = validatIxbrlAgainstTnep(ixbrl, location);

            if (hasPassedTnepValidation(results)) {
                addToLog(false, null, results, location,
                    "Ixbrl is valid. It has passed the TNEP validation");

                return true;

            } else {
                addToLog(true, null, results, location,
                    "Ixbrl is invalid. It has failed the TNEP validation");

                return false;
            }

        } catch (Exception e) {
            addToLog(true, e, null, location,
                "Exception has been thrown when calling TNEP validator. Unable to validate Ixbrl");

            return false;
        }
    }

    /**
     * Call TNEP validator service, via http POST using multipart file upload, to check if ixbrl is
     * valid.
     *
     * @param ixbrl - ixbrl content to be validated.
     * @param location - ixbrl location, public location.
     * @return {@link Results} with the information from calling the Tnep service.
     * @throws URISyntaxException
     */
    private Results validatIxbrlAgainstTnep(String ixbrl, String location)
        throws URISyntaxException {

        LinkedMultiValueMap<String, Object> map = createFileMessageResource(ixbrl, location);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = setHttpHeaders(map);

        return postForValidation(requestEntity);
    }

    private boolean hasPassedTnepValidation(Results results) {
        return results != null && "OK".equalsIgnoreCase(results.getValidationStatus());
    }

    /**
     * Connect to the TNEP validator via http POST using multipart file upload
     *
     * @return RestTemplate
     */
    private Results postForValidation(HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity)
        throws URISyntaxException {

        return restTemplate
            .postForObject(new URI(getIxbrlValidatorUri()), requestEntity, Results.class);
    }

    private void addToLog(boolean hasValidationFailed, Exception e, Results results,
        String location, String message) {

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("message", message);
        logMap.put("location", location);

        if (results != null) {
            logMap.put("results", results);
        }

        if (hasValidationFailed) {
            LOGGER.error("TnepValidationServiceImpl: validation has failed", e, logMap);
        } else {
            LOGGER.debug("TnepValidationServiceImpl: validation has passed", logMap);
        }
    }

    /**
     * Add http Header attributes for validation POST
     *
     * @Return HttpEntity<>(LinkedMultiValueMap<String, Object> , HttpHeaders);
     */
    private HttpEntity<LinkedMultiValueMap<String, Object>> setHttpHeaders(
        LinkedMultiValueMap<String, Object> map) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(map,
            headers);
    }

    private LinkedMultiValueMap<String, Object> createFileMessageResource(String ixbrl,
        String location) {
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", new FileMessageResource(ixbrl.getBytes(), location));

        return map;
    }

    /**
     * Obtain the URL of the TNEP validator from the environment
     *
     * @return String
     */
    protected String getIxbrlValidatorUri() {

        return environmentReader.getMandatoryString(IXBRL_VALIDATOR_URI);
    }

    private class FileMessageResource extends ByteArrayResource {

        /**
         * The filename to be associated with the {@link MimeMessage} in the form data.
         */
        private final String filename;

        /**
         * Constructs a new {@link FileMessageResource}.
         *
         * @param byteArray A byte array containing data from a {@link MimeMessage}.
         * @param filename The filename to be associated with the {@link MimeMessage} in the form
         * data.
         */
        public FileMessageResource(final byte[] byteArray, final String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }
}
