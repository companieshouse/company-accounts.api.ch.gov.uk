package uk.gov.companieshouse.api.accounts.utility.ixbrl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.Links;
import uk.gov.companieshouse.api.accounts.transaction.TransactionServiceProperties;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class DocumentGeneratorCallerTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String ACCOUNTS_RESOURCE_URI =
        "/transactions/" + TRANSACTION_ID + "/company-accounts/" + ACCOUNTS_ID;

    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String PERIOD_END_ON_KEY = "period_end_on";
    private static final String API_KEY_VALUE = "apiKeyValueForTesting";

    private DocumentGeneratorCaller documentGeneratorCaller;

    @Mock
    private RestTemplate restTemplateMock;
    @Mock
    private TransactionServiceProperties transactionServicePropertiesMock;

    @BeforeEach
    void setUpBeforeEach() {
        documentGeneratorCaller = new DocumentGeneratorCaller(restTemplateMock,
            transactionServicePropertiesMock);
    }

    @Test
    @DisplayName("Document Generator Caller generates the DocumentGeneratorResponse successfully. Correct status code returned")
    void shouldGenerateDocumentGeneratorResponseCallDocumentGeneratorIsSuccessful() {

        mockTransactionServiceProperties(API_KEY_VALUE);

        doReturn(createDocumentGeneratorResponseEntity(HttpStatus.CREATED))
            .when(restTemplateMock)
            .exchange(anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(DocumentGeneratorResponse.class));

        DocumentGeneratorResponse response = documentGeneratorCaller
            .callDocumentGeneratorService(ACCOUNTS_RESOURCE_URI);

        verifyRestTemplateMockNumOfCalls();
        assertNotNull(response);
    }

    @Test
    @DisplayName("Document Generator Caller fails to generate the DocumentGeneratorResponse. Wrong status code returned")
    void shouldNotGenerateDocumentGeneratorResponseCallDocumentGeneratorIsUnsuccessful() {

        mockTransactionServiceProperties(API_KEY_VALUE);

        doReturn(createDocumentGeneratorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR))
            .when(restTemplateMock)
            .exchange(anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(DocumentGeneratorResponse.class));

        DocumentGeneratorResponse response = documentGeneratorCaller
            .callDocumentGeneratorService(ACCOUNTS_RESOURCE_URI);

        verifyRestTemplateMockNumOfCalls();
        assertNull(response);
    }

    @Test
    @DisplayName("Document Generator Caller fails to generate the DocumentGeneratorResponse. An exception is thrown")
    void shouldNotGenerateDocumentGeneratorResponseDocumentGeneratorThrowsException() {

        mockTransactionServiceProperties(API_KEY_VALUE);

        when(restTemplateMock
            .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(DocumentGeneratorResponse.class)))
            .thenThrow(RestClientException.class);

        DocumentGeneratorResponse response = documentGeneratorCaller
            .callDocumentGeneratorService(ACCOUNTS_RESOURCE_URI);

        verifyRestTemplateMockNumOfCalls();
        assertNull(response);
    }

    @Test
    @DisplayName("Document Generator Caller fails when api key has not been set. Exception thrown")
    void shouldGenerateDocumentGeneratorThrowAnExceptionAsApiKeyNotSet() {

        mockTransactionServiceProperties("");
        assertThrows(IllegalArgumentException.class,
            () -> documentGeneratorCaller.callDocumentGeneratorService(ACCOUNTS_RESOURCE_URI));
    }

    private void mockTransactionServiceProperties(String apiKeyValue) {
        when(transactionServicePropertiesMock.getApiKey()).thenReturn(apiKeyValue);
    }

    /**
     * Verifies number of calls to the postForEntity.
     */
    private void verifyRestTemplateMockNumOfCalls() {
        verify(restTemplateMock, times(1))
            .exchange(anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(DocumentGeneratorResponse.class));
    }

    /**
     * creates dummy Document Generator response entity with the status passed in.
     *
     * @return ResponseEntity<> with the desired transaction
     */
    private ResponseEntity<DocumentGeneratorResponse> createDocumentGeneratorResponseEntity(
        HttpStatus httpStatus) {

        DocumentGeneratorResponse documentGeneratorResponse = createDocumentGeneratorResponse();

        return new ResponseEntity<>(documentGeneratorResponse, httpStatus);
    }


    /**
     * Create a Document Generator Response with all needed information to generate the filing.
     */
    private DocumentGeneratorResponse createDocumentGeneratorResponse() {
        DocumentGeneratorResponse documentGeneratorResponse = new DocumentGeneratorResponse();

        Links links = new Links();
        links.setLocation(IXBRL_LOCATION);
        documentGeneratorResponse.setLinks(links);

        Map<String, String> descriptionValues = new HashMap<>();
        descriptionValues.put(PERIOD_END_ON_KEY, "01 January 2018");
        documentGeneratorResponse.setDescriptionValues(descriptionValues);

        documentGeneratorResponse
            .setDescription("Small full accounts made up to 18 January 2018");

        documentGeneratorResponse.setDescriptionIdentifier("small-full-accounts");
        documentGeneratorResponse.setSize("999999");

        return documentGeneratorResponse;
    }
}
