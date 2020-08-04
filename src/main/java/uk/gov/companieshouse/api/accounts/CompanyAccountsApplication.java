package uk.gov.companieshouse.api.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
import uk.gov.companieshouse.api.accounts.interceptor.SmallFullInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.accounts.utility.AccountsNotesPathsYamlReader;

import java.util.Properties;
import uk.gov.companieshouse.api.accounts.utility.YamlResourceMapper;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;

@SpringBootApplication
public class CompanyAccountsApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "company-accounts.api.ch.gov.uk";

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Autowired
    private OpenTransactionInterceptor openTransactionInterceptor;

    @Autowired
    private ClosedTransactionInterceptor closedTransactionInterceptor;

    @Autowired
    private CompanyAccountInterceptor companyAccountInterceptor;

    @Autowired
    private SmallFullInterceptor smallFullInterceptor;

    @Autowired
    private CurrentPeriodInterceptor currentPeriodInterceptor;

    @Autowired
    private PreviousPeriodInterceptor previousPeriodInterceptor;

    @Autowired
    private CicReportInterceptor cicReportInterceptor;

    @Autowired
    private DirectorsReportInterceptor directorsReportInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;
    
    @Autowired
    private TokenPermissionsInterceptor tokenPermissionsInterceptor;
    
    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;
    
    @Autowired
    private LoansToDirectorsInterceptor loansToDirectorsInterceptor;

    public static void main(String[] args) {

        SpringApplication application = new SpringApplication(CompanyAccountsApplication.class);
        Properties properties = new Properties();

        AccountsNotesPathsYamlReader accountsNotesPathsYamlReader =
                new AccountsNotesPathsYamlReader(new YamlResourceMapper());

        accountsNotesPathsYamlReader.populatePropertiesFromYamlFile(properties);

        application.setDefaultProperties(properties);

        application.run(args);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {

        registry.addInterceptor(loggingInterceptor)
            .excludePathPatterns("/healthcheck");

        registry.addInterceptor(tokenPermissionsInterceptor)
            .addPathPatterns(
                    "/transactions/{transactionId}/company-accounts",
                    "/transactions/{transactionId}/company-accounts/**");

        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/transactions/{transactionId}/company-accounts",
                    "/transactions/{transactionId}/company-accounts/**");

        registry.addInterceptor(transactionInterceptor)
            .addPathPatterns(
                "/transactions/{transactionId}/**",
                "/private/transactions/{transactionId}/**");

        registry.addInterceptor(openTransactionInterceptor)
            .addPathPatterns(
                "/transactions/{transactionId}/**",
                "/private/transactions/{transactionId}/**")
            .excludePathPatterns(
                "/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings");

        registry.addInterceptor(closedTransactionInterceptor)
            .addPathPatterns(
                "/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings");

        // This {companyAccountId}/** has been added to re-direct to the CompanyAccountInterceptor the following urls:
        // "/company-accounts/{companyAccountId}"
        // "/company-accounts/{companyAccountId}/small-full"
        // "/company-accounts/{companyAccountId}/small-full/..."
        // Excluding url: "/company-accounts"
        registry.addInterceptor(companyAccountInterceptor)
            .addPathPatterns(
                "/transactions/{transactionId}/company-accounts/{companyAccountId}/**",
                "/private/transactions/{transactionId}/company-accounts/{companyAccountId}/**");

        registry.addInterceptor(smallFullInterceptor)
            .addPathPatterns(
                "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full",
                "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/**");

        registry.addInterceptor(currentPeriodInterceptor)
                .addPathPatterns("/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/current-period",
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/current-period/**");

        registry.addInterceptor(previousPeriodInterceptor)
                .addPathPatterns("/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period",
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period/**");

        registry.addInterceptor(cicReportInterceptor)
            .addPathPatterns(
                    "/transactions/{transactionId}/company-accounts/{companyAccountId}/cic-report/**")
            .excludePathPatterns(
                    "/transactions/{transactionId}/company-accounts/{companyAccountId}/cic-report");

        registry.addInterceptor(directorsReportInterceptor)
                .addPathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/directors-report/**")
                .excludePathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/directors-report");

        registry.addInterceptor(loansToDirectorsInterceptor)
                .addPathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/loans-to-directors/**")
                .excludePathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/loans-to-directors");
    }
}