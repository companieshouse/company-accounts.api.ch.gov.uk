package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@Component
public class TnepValidationServiceImpl implements TnepValidationService {

    private static final String IXBRL_VALIDATOR_URI = "IXBRL_VALIDATOR_URI";

    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private RestTemplate restTemplate;

    private EnvironmentReader environmentReader;
    
    @Autowired
    public TnepValidationServiceImpl(RestTemplate restTemplate, EnvironmentReader environmentReader) {
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
            //Connect to the TNEP validator via http POST using multipart file upload
            LinkedMultiValueMap<String, Object> map = createFileMessageResource(ixbrl, location);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = setHttpHeaders(map);

            Results results = postForValidation(requestEntity);

            return logValidationResponse(location, results);

        } catch (Exception e) {

            return logErroredValidation(location, e);
        }
    }

    /**
     * Log the response of the validation - Successful or failed
     *
     * @return boolean
     */
    private boolean logValidationResponse(String location, Results results) {
        if (results != null && "OK".equalsIgnoreCase(results.getValidationStatus())) {

            return logSuccessfulValidation(location, results);

        } else {

            return logFailedValidation(location, results);
        }
    }

    /**
     * Log any error caught whilst validating
     *
     * @return boolean
     */
    private boolean logErroredValidation(String location, Exception e) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("error", "Unable to validate ixbrl");
        logMap.put("location", location);
        LOG.error(e, logMap);

        return false;
    }

    private boolean logFailedValidation(String location, Results results) {
        Map<String, Object> logMap = generateLogMap(location, results);
        LOG.error("Ixbrl validation failed", logMap);

        return false;
    }

    private boolean logSuccessfulValidation(String location, Results results) {
        Map<String, Object> logMap = generateLogMap(location, results);
        LOG.debug("Ixbrl validation succeeded", logMap);

        return true;
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

    private Map<String, Object> generateLogMap(String location, Results results) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("location", location);
        logMap.put("results", results);

        return logMap;
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
         * @param filename  The filename to be associated with the {@link MimeMessage} in the form
         *                  data.
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

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
