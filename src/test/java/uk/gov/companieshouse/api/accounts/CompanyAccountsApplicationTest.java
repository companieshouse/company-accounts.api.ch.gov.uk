package uk.gov.companieshouse.api.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.accounts.interceptor.CicReportInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CompanyAccountInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CurrentPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.PreviousPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.SmallFullInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.TransactionInterceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyAccountsApplicationTest {

    @InjectMocks
    private CompanyAccountsApplication companyAccountsApplication;

    @Mock
    TransactionInterceptor transactionInterceptor;

    @Mock
    private OpenTransactionInterceptor openTransactionInterceptor;

    @Mock
    private ClosedTransactionInterceptor closedTransactionInterceptor;

    @Mock
    private CompanyAccountInterceptor companyAccountInterceptor;

    @Mock
    private SmallFullInterceptor smallFullInterceptor;

    @Mock
    private CurrentPeriodInterceptor currentPeriodInterceptor;

    @Mock
    private PreviousPeriodInterceptor previousPeriodInterceptor;

    @Mock
    private CicReportInterceptor cicReportInterceptor;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;



    @Test
    @DisplayName("Test if interceptors are added correctly")
    void testAddInterceptors() {

        when(interceptorRegistry.addInterceptor(any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(Mockito.<String>any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.excludePathPatterns(anyString())).thenReturn(interceptorRegistration);
        companyAccountsApplication.addInterceptors(interceptorRegistry);
        verify(interceptorRegistry, times(1)).addInterceptor(loggingInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(transactionInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(openTransactionInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(closedTransactionInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(companyAccountInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(smallFullInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(currentPeriodInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(previousPeriodInterceptor);
        verify(interceptorRegistry, times(1)).addInterceptor(cicReportInterceptor);
        verify(interceptorRegistration, times(3)).excludePathPatterns(anyString());
        verify(interceptorRegistration, times(6)).addPathPatterns(anyString(),anyString());
        verify(interceptorRegistration, times(2)).addPathPatterns(anyString());
    }

}
