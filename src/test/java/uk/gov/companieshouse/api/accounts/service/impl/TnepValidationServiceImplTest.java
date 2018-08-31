package uk.gov.companieshouse.api.accounts.service.impl;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class TnepValidationServiceImplTest {

    public static final String S3_TESTLOCATION = "s3://testlocation/testdoc";
    private TnepValidationServiceImpl tnepValidationService;

    private static final String TNEP_URL = "http://testtnep.companieshouse.gov.uk/validate";

    private MockRestServiceServer mockServer;

    private static final String VALIDATION_FAILURE_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<results validationStatus=\"FAILED\">"
            + "<errors>"
            + "<ErrorMessage>AccountsTypeFullOrAbbreviated must be provided for the current accounting period.</ErrorMessage>"
            + "</errors>"
            + "<data>"
            + "<BalanceSheetDate>2016-12-31</BalanceSheetDate>"
            + "<AccountsType>08</AccountsType>"
            + "<CompaniesHouseRegisteredNumber>00006400</CompaniesHouseRegisteredNumber>"
            + "</data>"
            + "</results>";

    private static final String VALIDATION_SUCCESS_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<results validationStatus=\"OK\">"
            + "<data>"
            + "<BalanceSheetDate>2016-12-31</BalanceSheetDate>"
            + "<AccountsType>08</AccountsType>"
            + "<CompaniesHouseRegisteredNumber>00006400</CompaniesHouseRegisteredNumber>"
            + "</data>"
            + "</results>";


    @Before
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
    public void validationSuccess() {

        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(VALIDATION_SUCCESS_RESPONSE, MediaType.APPLICATION_XML));

        boolean result = tnepValidationService.validate("test", S3_TESTLOCATION);

        assertTrue(result);

        mockServer.verify();
    }


    @Test
    public void validationFailure() {

        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess(VALIDATION_FAILURE_RESPONSE, MediaType.APPLICATION_XML));

        boolean result = tnepValidationService.validate("test", S3_TESTLOCATION);

        assertFalse(result);

        mockServer.verify();
    }


    @Test
    public void invalidResponse() {
        mockServer.expect(requestTo(TNEP_URL))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("", MediaType.APPLICATION_XML));

        boolean result = tnepValidationService.validate("test", S3_TESTLOCATION);

        assertFalse(result);

        mockServer.verify();
    }

    @Test
    public void missingEnvVariable() {

        tnepValidationService = new TnepValidationServiceImpl() {

            protected String getIxbrlValidatorUriEnvVal() {
                return null;
            }
        };

        boolean result = tnepValidationService.validate("test", S3_TESTLOCATION);

        assertFalse(result);

    }
}
