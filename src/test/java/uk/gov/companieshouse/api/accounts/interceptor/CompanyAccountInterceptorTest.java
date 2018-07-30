package uk.gov.companieshouse.api.accounts.interceptor;

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
import org.apache.commons.lang.StringUtils;
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
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountInterceptorTest {

    @Mock
    private HttpSession session;
    @Mock
    private CompanyAccountEntity companyAccountEntity;
    @Mock
    private CompanyAccountService companyAccountService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @InjectMocks
    private CompanyAccountInterceptor companyAccountInterceptor;

    @BeforeEach
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(httpServletRequest.getRequestURI()).thenReturn("/transactions/123/company-accounts/456");
        when(companyAccountService.findByExample(any(CompanyAccountEntity.class))).thenReturn(companyAccountEntity);
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly after a specific URI is provided")
    public void testReturnsTheCorrectEntityForTheURI() {
        companyAccountInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(companyAccountService, times(1)).findByExample(any(CompanyAccountEntity.class));
        verify(session, times(1)).setAttribute(anyString(), any(CompanyAccountEntity.class));
    }
}
