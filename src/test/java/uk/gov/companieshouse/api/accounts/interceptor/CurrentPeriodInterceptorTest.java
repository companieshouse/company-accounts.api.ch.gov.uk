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
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CurrentPeriodInterceptorTest {
    @Mock
    private CurrentPeriod currentPeriod;

    @Mock
    private ResponseObject<CurrentPeriod> responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> currentPeriodLinks;

    @InjectMocks
    private CurrentPeriodInterceptor currentPeriodInterceptor;

    private static final String TRANSACTION_ID = "transactionId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private  static final String X_REQUEST_ID = "X-Request-Id";
    private static final String LINK_CURRENT_PERIOD = "linkToCurrentPeriod";
    private static final String NO_LINK_CURRENT_PERIOD = "noLinkToPreviousPeriod";
    private static final String URI = "../../../current-period";

    public void setUpServletRequest() {
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
        setUpServletRequest();
        when(currentPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(currentPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(currentPeriod.getLinks()).thenReturn(currentPeriodLinks);
        when(smallFullLinks.get(SmallFullLinkType.CURRENT_PERIOD.getLink())).thenReturn(LINK_CURRENT_PERIOD);
        when(currentPeriodLinks.get(CurrentPeriodLinkType.SELF.getLink())).thenReturn(LINK_CURRENT_PERIOD);

        currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(currentPeriodService, times(1)).find(anyString(), any(HttpServletRequest.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(CurrentPeriod.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed CurrentPeriodEntity lookup")
    void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {
        setUpServletRequest();
        doThrow(mock(DataException.class)).when(currentPeriodService).find(anyString(), any(HttpServletRequest.class));
        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));
    }

    @Test
    @DisplayName(("Test the interceptor returns true when URI ends with current-period"))
    void testReturnTrueForURIEndsWithCurrentPeriod() throws NoSuchAlgorithmException {
        when(httpServletRequest.getMethod()).thenReturn("POST");
        when(httpServletRequest.getRequestURI()).thenReturn(URI);

        assertTrue(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));
    }

    @Test
    @DisplayName(("Test the interceptor when transaction is null"))
    void testTransactionIsNull() throws NoSuchAlgorithmException {
        when(httpServletRequest.getMethod()).thenReturn("GET");
        doReturn(null).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());

        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

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

        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Current Period interceptor - returns not found status")
    void currentPeriodInterceptorReturnsNotFoundStatus() throws DataException, NoSuchAlgorithmException {
        setUpServletRequest();
        when(currentPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Current Period interceptor - returns bad request status")
    void currentPeriodInterceptorReturnsBadRequestStatus() throws DataException, NoSuchAlgorithmException {
        setUpServletRequest();
        when(currentPeriodService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(currentPeriod);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(currentPeriod.getLinks()).thenReturn(currentPeriodLinks);
        when(currentPeriodLinks.get("self")).thenReturn(LINK_CURRENT_PERIOD);
        when(smallFullLinks.get(SmallFullLinkType.CURRENT_PERIOD.getLink())).thenReturn(NO_LINK_CURRENT_PERIOD);

        assertFalse(currentPeriodInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
