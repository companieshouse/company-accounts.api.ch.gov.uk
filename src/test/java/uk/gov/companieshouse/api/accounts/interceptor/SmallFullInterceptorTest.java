package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
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
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullInterceptorTest {

    @Mock
    private HttpSession session;
    @Mock
    private SmallFullEntity smallFullEntity;
    @Mock
    private SmallFullDataEntity smallFullDataEntity;
    @Mock
    private Transaction transaction;
    @Mock
    private CompanyAccountEntity companyAccountEntity;
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

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException {
        when(httpServletRequest.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(transaction).thenReturn(companyAccountEntity);
        when(smallFullService.findById(anyString())).thenReturn(smallFullEntity);
        when(companyAccountEntity.getId()).thenReturn("12345");
        when(smallFullService.generateID(anyString())).thenReturn("123456");
        when(httpServletRequest.getMethod()).thenReturn("GET");
    }

    @Test
    @DisplayName("Tests the interceptor returns correctly when all is valid")
    public void testReturnsCorrectlyOnValidConditions() throws NoSuchAlgorithmException {
        when(companyAccountEntity.getData()).thenReturn(companyAccountDataEntity);
        when(companyAccountDataEntity.getLinks()).thenReturn(companyAccountLinks);
        when(smallFullEntity.getData()).thenReturn(smallFullDataEntity);
        when(smallFullDataEntity.getLinks()).thenReturn(smallFullLinks);
        when(companyAccountLinks.get("small_full_accounts")).thenReturn("linkToSmallFull");
        when(smallFullLinks.get("self")).thenReturn("linkToSmallFull");
        smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object());
        verify(smallFullService, times(1)).findById(anyString());
        verify(session, times(1)).setAttribute(anyString(), any(SmallFullEntity.class));
    }

    @Test
    @DisplayName("Tests the interceptor returns false on a failed SmallFullEntity lookup")
    public void testReturnsFalseForAFailedLookup() throws NoSuchAlgorithmException {
        when(smallFullService.findById(anyString())).thenReturn(null);
        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor returns false when the two links do not match")
    public void testReturnsFalseForLinksThatDoNotMatch() throws NoSuchAlgorithmException {
        when(smallFullService.findById(anyString())).thenReturn(null);
        assertFalse(smallFullInterceptor.preHandle(httpServletRequest, httpServletResponse,
                new Object()));
    }
}