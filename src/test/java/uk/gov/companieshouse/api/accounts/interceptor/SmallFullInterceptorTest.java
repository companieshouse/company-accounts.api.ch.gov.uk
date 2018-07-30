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
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullInterceptorTest {

    @Mock
    private HttpSession session;
    @Mock
    private SmallFullEntity smallFullEntity;
    @Mock
    private SmallFullService smallFullService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @InjectMocks
    private SmallFullInterceptor smallFullInterceptor;

    @BeforeEach
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(httpServletRequest.getRequestURI()).thenReturn("/transactions/123/company-accounts/456/small-full");
        when(smallFullService.findByExample(any(SmallFullEntity.class))).thenReturn(smallFullEntity);
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly after a specific URI is provided")
    public void testReturnsTheCorrectEntityForTheURI() {
        smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(smallFullService, times(1)).findByExample(any(SmallFullEntity.class));
        verify(session, times(1)).setAttribute(anyString(), any(SmallFullEntity.class));
    }
}
