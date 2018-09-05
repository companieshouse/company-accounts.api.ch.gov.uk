package uk.gov.companieshouse.api.accounts.service.impl;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class TnepValidationServiceImplTest {

    private TnepValidationServiceImpl tnepValidationService;
    private MockRestServiceServer mockServer;

    private static final String S3_TESTLOCATION = "s3://testlocation/testdoc";
    private static final String TNEP_URL = "http://testtnep.companieshouse.gov.uk/validate";

    @BeforeEach
    public void setUp() {

        tnepValidationService = new TnepValidationServiceImpl() {
            @Override
            protected String getIxbrlValidatorUri() {
                return TNEP_URL;
            }
        };

        RestTemplate restTemplate = new RestTemplate();
        tnepValidationService.setRestTemplate(restTemplate);

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void validationSuccess() throws IOException {

        String xmlSuccessResponse = IOUtils
            .toString(this.getClass().getResourceAsStream("/validation-success.xml"), "UTF-8");

        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(xmlSuccessResponse, MediaType.APPLICATION_XML));

        assertTrue(validateIxbrl());

        mockServer.verify();
    }

    @Test
    public void validationFailure() throws IOException {

        String xmlFailureResponse = IOUtils
            .toString(this.getClass().getResourceAsStream("/validation-failure.xml"), "UTF-8");

        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(xmlFailureResponse, MediaType.APPLICATION_XML));

        assertFalse(validateIxbrl());

        mockServer.verify();
    }

    @Test
    public void invalidResponse() {

        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("", MediaType.APPLICATION_XML));

        assertFalse(validateIxbrl());

        mockServer.verify();
    }

    @Test
    public void missingEnvVariable() {

        tnepValidationService = new TnepValidationServiceImpl() {

            protected String getIxbrlValidatorUriEnvVal() {
                return null;
            }
        };

        assertFalse(validateIxbrl());
    }

    private boolean validateIxbrl() {
        return tnepValidationService.validate("test", S3_TESTLOCATION);
    }
}
