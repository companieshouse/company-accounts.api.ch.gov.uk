package uk.gov.companieshouse.api.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.accounts.interceptor.AuthenticationInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CicReportInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CompanyAccountInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CurrentPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.DirectorsReportInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.LoansToDirectorsInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.PreviousPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.RelatedPartyTransactionsInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.SmallFullInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CompanyAccountsApplicationTest {

    @InjectMocks
    private CompanyAccountsApplication companyAccountsApplication;

    @Mock
    private TransactionInterceptor transactionInterceptor;

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
    private DirectorsReportInterceptor directorsReportInterceptor;

    @Mock
    private LoansToDirectorsInterceptor loansToDirectorsInterceptor;

    @Mock
    private RelatedPartyTransactionsInterceptor relatedPartyTransactionsInterceptor;

    @Mock
    private LoggingInterceptor loggingInterceptor;

    @Mock
    private TokenPermissionsInterceptor tokenPermissionsInterceptor;

    @Mock
    private AuthenticationInterceptor authenticationInterceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Test
    @DisplayName("Test if interceptors are added correctly")
    void testAddInterceptors() {
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(loggingInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(tokenPermissionsInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(authenticationInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(transactionInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(openTransactionInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(closedTransactionInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(companyAccountInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(smallFullInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(currentPeriodInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(previousPeriodInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(cicReportInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(directorsReportInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(loansToDirectorsInterceptor);
        doReturn(interceptorRegistration).when(interceptorRegistry).addInterceptor(relatedPartyTransactionsInterceptor);
        when(interceptorRegistration.addPathPatterns(Mockito.<String>any())).thenReturn(interceptorRegistration);
        when(interceptorRegistration.excludePathPatterns(anyString())).thenReturn(interceptorRegistration);

        companyAccountsApplication.addInterceptors(interceptorRegistry);

        InOrder inOrder = Mockito.inOrder(interceptorRegistry);
        inOrder.verify(interceptorRegistry).addInterceptor(tokenPermissionsInterceptor);
        inOrder.verify(interceptorRegistry).addInterceptor(authenticationInterceptor);

        verifyNoMoreInteractions(interceptorRegistry);
    }

}
