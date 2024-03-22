package uk.gov.companieshouse.api.accounts.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.transaction.TransactionsResourceHandler;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class TransactionInterceptorTest {

    @InjectMocks
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private ApiClientService apiClientServiceMock;

    @Mock
    private ApiClient apiClientMock;

    @Mock
    private TransactionsResourceHandler transactionResourceHandlerMock;

    @Mock
    private TransactionsGet transactionGetMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @Mock
    private HttpClientErrorException httpClientErrorException;

    @Mock
    private ApiResponse<Transaction> apiResponse;

    @BeforeEach
    void setUp() throws URIValidationException, IOException {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");

        when(httpServletRequestMock.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariables);
        when(httpServletRequestMock.getHeader("ERIC-Access-Token")).thenReturn("1111");

        httpServletResponseMock.setContentType("text/html");

        when(apiClientServiceMock.getApiClient(anyString())).thenReturn(apiClientMock);
        when(apiClientMock.transactions()).thenReturn(transactionResourceHandlerMock);
        when(transactionResourceHandlerMock.get(anyString())).thenReturn(transactionGetMock);
        when(transactionGetMock.execute()).thenReturn(apiResponse);
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction")
    void testPreHandleExistingTransaction() {

        assertTrue(transactionInterceptor
            .preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Transaction interceptor - throws HttpClientErrorException")
    void testPreHandleThrowsHttpClientErrorException() throws HttpClientErrorException, ApiErrorResponseException, URIValidationException {

        when(transactionGetMock.execute()).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        assertFalse(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Transaction interceptor - throws ApiErrorResponseException")
    void testPreHandleThrowsApiErrorResponseException() throws ApiErrorResponseException, URIValidationException {

        when(transactionGetMock.execute()).thenThrow(ApiErrorResponseException.class);

        assertFalse(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Transaction interceptor - throws URIValidationException")
    void testPreHandleThrowsURIValidationException() throws ApiErrorResponseException, URIValidationException {

        when(transactionGetMock.execute()).thenThrow(URIValidationException.class);

        assertFalse(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));

        verify(httpServletResponseMock).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}