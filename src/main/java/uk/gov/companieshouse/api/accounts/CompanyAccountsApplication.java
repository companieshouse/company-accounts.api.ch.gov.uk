package uk.gov.companieshouse.api.accounts;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.accounts.interceptor.CicReportInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.ClosedTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CompanyAccountInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.CurrentPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.OpenTransactionInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.PreviousPeriodInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.SmallFullInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.api.accounts.utility.AccountResourcePathsYamlReader;

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
    private LoggingInterceptor loggingInterceptor;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CompanyAccountsApplication.class);

        Properties properties = new Properties();

        AccountResourcePathsYamlReader reader = new AccountResourcePathsYamlReader(properties);
        reader.populatePropertiesFromYamlFile();

        application.setDefaultProperties(properties);

        application.run(args);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {

        registry.addInterceptor(loggingInterceptor)
            .excludePathPatterns("/healthcheck");

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
    }
}