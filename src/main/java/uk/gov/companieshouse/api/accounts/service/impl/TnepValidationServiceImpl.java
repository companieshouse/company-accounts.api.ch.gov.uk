package uk.gov.companieshouse.api.accounts.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.exception.handler.MissingEnvironmentVariableException;
import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Component
public class TnepValidationServiceImpl implements TnepValidationService {

    private static final String IXBRL_VALIDATOR_URI = "IXBRL_VALIDATOR_URI";

    private static final Logger LOG = LoggerFactory.getLogger("company-accounts.api.ch.gov.uk");

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Validate the ixbrl
     *
     * @return boolean
     */
    @Override
    public boolean validate(String ixbrl, String location) {

        try {
            //Connect to the TNEP validator via http POST using multipart file upload
            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
            map.add("file", new FileMessageResource(ixbrl.getBytes(), location));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map,
                headers);

            Results results = restTemplate
                .postForObject(new URI(getIxbrlValidatorUri()), requestEntity, Results.class);

            if (results != null && "OK".equalsIgnoreCase(results.getValidationStatus())) {
                Map<String, Object> logMap = generateLogMap(location, results);
                LOG.debug("Ixbrl validation succeeded", logMap);

                return true;
            } else {
                Map<String, Object> logMap = generateLogMap(location, results);
                LOG.error("Ixbrl validation failed", logMap);

                return false;
            }

        } catch (
            Exception e)

        {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("error", "Unable to validate ixbrl");
            logMap.put("location", location);
            LOG.error(e, logMap);

            return false;
        }
    }

    /**
     * Obtain the URL of the TNEP validator from the environment
     *
     * @return String
     */
    protected String getIxbrlValidatorUri() {

        String ixbrlValidatorUri = System.getenv(IXBRL_VALIDATOR_URI);
        if(StringUtils.isBlank(ixbrlValidatorUri)) {
            throw new MissingEnvironmentVariableException("Missing IXBRL_VALIDATOR_URI environment variable");

        }

        return ixbrlValidatorUri;
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
}
