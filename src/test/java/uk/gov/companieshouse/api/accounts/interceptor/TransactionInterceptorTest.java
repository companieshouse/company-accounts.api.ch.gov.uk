package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionGet;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TransactionInterceptorTest {

    @InjectMocks
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private ApiClientService apiClientServiceMock;

    @Mock
    private InternalApiClient internalApiClientMock;

    @Mock
    private PrivateTransactionResourceHandler transactionResourceHandlerMock;

    @Mock
    private PrivateTransactionGet transactionGetMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @BeforeEach
    void setUp() throws URIValidationException, IOException {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");

        when(httpServletRequestMock.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
            .thenReturn(pathVariables);
        when(httpServletRequestMock.getHeader("ERIC-Access-Token")).thenReturn("1111");

        httpServletResponseMock.setContentType("text/html");

        when(apiClientServiceMock.getInternalApiClient(anyString())).thenReturn(internalApiClientMock);
        when(internalApiClientMock.privateTransaction()).thenReturn(transactionResourceHandlerMock);
        when(transactionResourceHandlerMock.get(anyString())).thenReturn(transactionGetMock);
        when(transactionGetMock.execute()).thenReturn(new Transaction());
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction")
    void testPreHandleExistingTransaction() throws URIValidationException, ApiErrorResponseException {

        assertTrue(transactionInterceptor
            .preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }
}