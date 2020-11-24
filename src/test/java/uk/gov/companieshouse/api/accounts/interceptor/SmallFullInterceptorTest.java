package uk.gov.companieshouse.api.accounts.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class SmallFullInterceptorTest {

    @Mock
    private SmallFull smallFull;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private CompanyAccountDataEntity companyAccountDataEntity;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> companyAccountLinks;

    @Mock
    private Map<String, String> smallFullLinks;

    @InjectMocks
    private SmallFullInterceptor smallFullInterceptor;

    private static final String TRANSACTION_ID = "transactionId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private static final String LINK_SMALL_FULL = "linkToSmallFull";
    private static final String NO_LINK_SMALL_FULL = "noLinkToSmallFull";
    private static final String URI = "../../../small-full";

    private void setUp() throws NoSuchAlgorithmException {

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");
        pathVariables.put("companyAccountId", "test");

        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("test");
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(companyAccount).when(httpServletRequest).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        when(httpServletRequest.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException, DataException {

        setUp();
        when(smallFullService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(smallFull);
        when(companyAccount.getLinks()).thenReturn(companyAccountLinks);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(companyAccountLinks.get("small_full_accounts")).thenReturn("linkToSmallFull");
        when(smallFullLinks.get("self")).thenReturn("linkToSmallFull");

        smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(smallFullService, times(1)).find(anyString(), any(HttpServletRequest.class));
        verify(httpServletRequest, times(1)).setAttribute(anyString(), any(SmallFull.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed SmallFullEntity lookup")
    void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException, DataException {

        setUp();
        doThrow(mock(DataException.class)).when(smallFullService).find(anyString(), any(HttpServletRequest.class));
        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

    @Test
    @DisplayName(("Test the interceptor returns true when URI ends with small-full"))
    void testReturnTrueForURIEndsWithSmallFull() throws NoSuchAlgorithmException {

        when(httpServletRequest.getMethod()).thenReturn("POST");
        when (httpServletRequest.getRequestURI()).thenReturn(URI);

        assertTrue(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

    @Test
    @DisplayName(("Test the interceptor when transaction is null"))
    void testTransactionIsNull() throws NoSuchAlgorithmException {

        when(httpServletRequest.getMethod()).thenReturn("GET");
        doReturn(null).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());

        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName(("Test the interceptor when companyAccount is null"))
    void testCompanyAccountIsNull() throws NoSuchAlgorithmException {

        transaction = mock(Transaction.class);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(TRANSACTION_ID, "5555");
        pathVariables.put(COMPANY_ACCOUNT_ID, "test");

        when(httpServletRequest.getMethod()).thenReturn("GET");
        doReturn(pathVariables).when(httpServletRequest).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        doReturn(transaction).when(httpServletRequest).getAttribute(AttributeName.TRANSACTION.getValue());

        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Small Full interceptor - returns Not Found status")
    void smallFullInterceptorReturnsNotFoundStatus() throws DataException, NoSuchAlgorithmException {

        setUp();
        when(smallFullService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Small Full interceptor - returns Bad Request status")
    void smallFullInterceptorReturnsBadRequestStatus() throws DataException, NoSuchAlgorithmException {

        setUp();
        when(smallFullService.find(anyString(), any(HttpServletRequest.class))).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(smallFull);
        when(companyAccount.getLinks()).thenReturn(companyAccountLinks);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(companyAccountLinks.get("small_full_accounts")).thenReturn(LINK_SMALL_FULL);
        when(smallFullLinks.get("self")).thenReturn(NO_LINK_SMALL_FULL);

        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));

        verify(httpServletResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}