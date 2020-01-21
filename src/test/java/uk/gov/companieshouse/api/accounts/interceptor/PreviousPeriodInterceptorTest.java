package uk.gov.companieshouse.api.accounts.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
  class PreviousPeriodInterceptorTest {


    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> previousPeriodLinks;

    @InjectMocks
    private PreviousPeriodInterceptor previousPeriodInterceptor;

    private static final String TRANSACTION_ID = "transactionId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private  static final String X_REQUEST_ID = "X-Request-Id";
    private static final String LINK_PREVIOUS_PERIOD = "linkToPreviousPeriod";
    private static final String NO_LINK_PREVIOUS_PERIOD = "noLinkToPreviousPeriod";
    private static final String URI = "../../../previous-period";

    void setUp() throws NoSuchAlgorithmException {

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(TRANSACTION_ID, "5555");
        pathVariables.put(COMPANY_ACCOUNT_ID, "test");

        when(httpServletRequest.getHeader(X_REQUEST_ID)).thenReturn("test");
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(httpServletRequest).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        when(httpServletRequest.getMethod()).thenReturn("GET");

    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException, DataException {

        setUp();
        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(previousPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(previousPeriod.getLinks()).thenReturn(previousPeriodLinks);
        when(smallFullLinks.get(SmallFullLinkType.PREVIOUS_PERIOD.getLink())).thenReturn(LINK_PREVIOUS_PERIOD);
        when(previousPeriodLinks.get("self")).thenReturn(LINK_PREVIOUS_PERIOD);

        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(previousPeriodService, times(1)).find(anyString(), any(HttpServletRequest.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(PreviousPeriod.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed PreviousPeriodEntity lookup")
    void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {
        setUp();
        doThrow(mock(DataException.class)).when(previousPeriodService).find(anyString(), any(HttpServletRequest.class));
        assertFalse(previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

    @Test
    @DisplayName(("Test the interceptor returns true when URI ends with previous-period"))
    void testReturnTrueForURIEndsWithPreviousPeriod() throws NoSuchAlgorithmException {

        when(httpServletRequest.getMethod()).thenReturn("POST");
        when (httpServletRequest.getRequestURI()).thenReturn(URI);

       boolean preHandle =  previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());

        assertTrue(preHandle);
    }

    @Test
    @DisplayName(("Test the interceptor when transaction is null"))
    void testTransactionIsNull() throws NoSuchAlgorithmException {

        when(httpServletRequest.getMethod()).thenReturn("GET");
        doReturn(null).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());
        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName(("Test the interceptor when smallFull is null"))
    void testSmallFullIsNull() throws NoSuchAlgorithmException {

        transaction = mock(Transaction.class);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(TRANSACTION_ID, "5555");
        pathVariables.put(COMPANY_ACCOUNT_ID, "test");

        when(httpServletRequest.getMethod()).thenReturn("GET");
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());

        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Previous Period interceptor - returns not found status")
    void previousPeriodInterceptorReturnsNotFoundStatus() throws DataException, NoSuchAlgorithmException {

        setUp();
        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Previous Period interceptor - returns bad request status")
    void previousPeriodInterceptorReturnsBadRequestStatus() throws DataException, NoSuchAlgorithmException {

        setUp();
        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(previousPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(previousPeriod.getLinks()).thenReturn(previousPeriodLinks);
        when(previousPeriodLinks.get("self")).thenReturn(LINK_PREVIOUS_PERIOD);
        when(smallFullLinks.get(SmallFullLinkType.PREVIOUS_PERIOD.getLink())).thenReturn(NO_LINK_PREVIOUS_PERIOD);

        previousPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
