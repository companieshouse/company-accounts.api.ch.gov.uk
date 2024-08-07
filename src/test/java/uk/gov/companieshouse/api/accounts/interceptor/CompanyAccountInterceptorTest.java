package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CompanyAccountInterceptorTest {

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private ResponseObject<CompanyAccount> responseObject;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountService companyAccountService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private Map<String, String> companyAccountslinks;

    @InjectMocks
    private CompanyAccountInterceptor companyAccountInterceptor;

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    void testReturnsCorrectlyOnValidConditions() throws DataException {
        setUpPathVariables();
        setUpReourceList("linkToCompanyAccount");
        setUpCompanyAccount();
        when(companyAccountService.findById("123456", httpServletRequest)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(companyAccount);

        assertTrue(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById("123456", httpServletRequest);
        verify(httpServletRequest, times(1))
                .setAttribute(anyString(), any(CompanyAccount.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed CompanyAccountEntity lookup")
    void testReturnsFalseForATransactionIsNull() {
        setUpPathVariables();
        when(httpServletRequest.getAttribute(AttributeName.TRANSACTION.getValue()))
                .thenReturn(null);
        assertFalse(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed CompanyAccountEntity lookup")
    void testReturnsFalseForAFailedLookup() throws DataException {
        setUpPathVariables();
        when(companyAccountService.findById("123456", httpServletRequest)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);
        assertFalse(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById("123456", httpServletRequest);
    }

    @Test
    @DisplayName("Tests the interceptor returns false when the two links do not match")
    void testReturnsFalseForLinksThatDoNotMatch() throws DataException {
        setUpPathVariables();
        setUpReourceList("badLink");
        setUpCompanyAccount();
        when(companyAccountService.findById("123456", httpServletRequest)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(companyAccount);
        assertFalse(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById("123456", httpServletRequest);
    }

    public void setUpReourceList(String linkToAdd) {
        Map<String, Resource> resourcesList = new HashMap<>();
        Map<String, String> link = new HashMap<>();
        link.put("resource", linkToAdd);
        Resource resource = new Resource();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());
        resource.setLinks(link);
        resourcesList.put("", resource);
        when(transaction.getResources()).thenReturn(resourcesList);
    }

    public void setUpCompanyAccount() {
        when(companyAccount.getLinks()).thenReturn(companyAccountslinks);
        when(companyAccountslinks.get("self")).thenReturn("linkToCompanyAccount");
    }

    public void setUpPathVariables() {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("companyAccountId", "123456");
        when(httpServletRequest.getAttribute(anyString())).thenReturn(transaction)
            .thenReturn(pathVariables);
    }
}