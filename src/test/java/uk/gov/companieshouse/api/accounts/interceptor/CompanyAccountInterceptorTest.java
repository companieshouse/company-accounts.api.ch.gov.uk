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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Resources;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountInterceptorTest {

    @Mock
    private HttpSession session;
    @Mock
    private CompanyAccountEntity companyAccountEntity;
    @Mock
    private CompanyAccountDataEntity companyAccountDataEntity;
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

    @BeforeEach
    public void setUp() {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("companyAccountId", "123456");
        when(httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(pathVariables);
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(companyAccountService.findById(anyString())).thenReturn(companyAccountEntity);
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    public void testReturnsCorrectlyOnValidConditions() {
        Map<String, Resources> resourcesList = new HashMap<>();
        Map<String, String> link = new HashMap<>();
        link.put("resource", "linkToCompanyAccount");
        Resources resource = new Resources();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());
        resource.setLinks(link);
        resourcesList.put("", resource);
        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);
        when(companyAccountDataEntity.getLinks()).thenReturn(companyAccountslinks);
        when(companyAccountService.findById(anyString())).thenReturn(companyAccountEntity);
        when(companyAccountslinks.get("self")).thenReturn("linkToCompanyAccount");
        when(transaction.getResources()).thenReturn(resourcesList);
        assertTrue(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById(anyString());
        verify(session, times(1)).setAttribute(anyString(), any(CompanyAccountEntity.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed CompanyAccountEntity lookup")
    public void testReturnsFalseForAFailedLookup() {
        when(companyAccountService.findById(anyString())).thenReturn(null);
        assertFalse(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById(anyString());
    }

    @Test
    @DisplayName("Tests the interceptor returns false when the two links do not match")
    public void testReturnsFalseForLinksThatDoNotMatch() {
        when(companyAccountService.findById(anyString())).thenReturn(companyAccountEntity);
        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);
        when(companyAccountDataEntity.getLinks()).thenReturn(companyAccountslinks);
        when(companyAccountslinks.get("self")).thenReturn("linkToCompanyAccount");
        Map<String, Resources> resourcesList = new HashMap<>();
        Map<String, String> link = new HashMap<>();
        link.put("resource", "badLink");
        Resources resource = new Resources();
        resource.setKind(Kind.COMPANY_ACCOUNTS.getValue());
        resource.setLinks(link);
        resourcesList.put("", resource);
        when(transaction.getResources()).thenReturn(resourcesList);
        assertFalse(companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
        verify(companyAccountService, times(1)).findById(anyString());
    }

}