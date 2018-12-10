package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TnepValidationServiceImplTest {

    private static final String ENV_VARIABLE_IXBRL_VALIDATOR_URI = "IXBRL_VALIDATOR_URI";
    private static final String ENV_VARIABLE_IXBRL_VALIDATOR_URI_VALUE = "http://tnep.url/validate";
    private static final String IXBRL_LOCATION = "s3://test-bucket/accounts/ixbrl-generated-name.html";
    private static final String IXBRL = getIxbrl();
    private static final String VALIDATION_STATUS_UNIT_TEST_FAILURE = "unit test failure";
    private static final String VALIDATION_STATUS_OK = "OK";

    @Mock
    RestTemplate restTemplate;
    @Mock
    EnvironmentReader environmentReader;

    private TnepValidationServiceImpl tnepValidationService;

    @BeforeEach
    void setup() {
        tnepValidationService = new TnepValidationServiceImpl(restTemplate, environmentReader);
    }

    @Test
    @DisplayName("Tnep validation call is successful. Happy path")
    void validationSuccess() {

        Results results = new Results();
        results.setValidationStatus(VALIDATION_STATUS_OK);

        mockEnvironmentReaderGetMandatoryString(ENV_VARIABLE_IXBRL_VALIDATOR_URI_VALUE);

        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class)))
            .thenReturn(results);

        assertTrue(validateIxbrl());
    }

    @Test
    @DisplayName("Tnep validation fails due to unit test failure")
    void validationFailure() {

        Results results = new Results();
        results.setValidationStatus(VALIDATION_STATUS_UNIT_TEST_FAILURE);

        mockEnvironmentReaderGetMandatoryString(ENV_VARIABLE_IXBRL_VALIDATOR_URI_VALUE);

        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class)))
            .thenReturn(results);

        assertFalse(validateIxbrl());
    }

    @Test
    void validationMissingResponse() {

        mockEnvironmentReaderGetMandatoryString(ENV_VARIABLE_IXBRL_VALIDATOR_URI_VALUE);

        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class)))
            .thenReturn(null);

        assertFalse(validateIxbrl());
    }

    @Test
    void invalidResponse() {

        mockEnvironmentReaderGetMandatoryString(ENV_VARIABLE_IXBRL_VALIDATOR_URI_VALUE);

        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class)))
            .thenThrow(new RestClientException(VALIDATION_STATUS_UNIT_TEST_FAILURE));

        assertFalse(validateIxbrl());
    }

    @Test
    void missingEnvVariable() {

        mockEnvironmentReaderGetMandatoryString(null);
        assertFalse(validateIxbrl());
    }

    private void mockEnvironmentReaderGetMandatoryString(String returnedMandatoryValue) {

        when(environmentReader.getMandatoryString(ENV_VARIABLE_IXBRL_VALIDATOR_URI))
            .thenReturn(returnedMandatoryValue);
    }

    private boolean validateIxbrl() {
        return tnepValidationService.validate(IXBRL, IXBRL_LOCATION);
    }

    private static String getIxbrl() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<html xmlns:ixt2=\"http://www.xbrl.org/inlineXBRL/transformation/2011-07-31\">\n"
            + "  <head>\n"
            + "    <meta content=\"application/xhtml+xml; charset=UTF-8\" http-equiv=\"content-type\" />\n"
            + "    <title>\n"
            + "            TEST COMPANY\n"
            + "        </title>\n"
            + "  <body xml:lang=\"en\">\n"
            + "    <div class=\"accounts-body \">\n"
            + "      <div id=\"your-account-type\" class=\"wholedoc\">\n"
            + "      </div>\n"
            + "    </div>\n"
            + "   </body>\n"
            + "</html>\n";
    }
}
