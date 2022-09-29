package uk.gov.companieshouse.api.accounts;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.accountsdates.AccountsDatesHelper;
import uk.gov.companieshouse.accountsdates.impl.AccountsDatesHelperImpl;
import uk.gov.companieshouse.api.accounts.interceptor.AuthenticationInterceptor;
import uk.gov.companieshouse.api.interceptor.TokenPermissionsInterceptor;
import uk.gov.companieshouse.charset.validation.CharSetValidation;
import uk.gov.companieshouse.charset.validation.impl.CharSetValidationImpl;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

/**
 * General application configuration .
 */
@Configuration
@PropertySource("classpath:ValidationMessages.properties")
public class ApplicationConfiguration {

    @Bean
    public CharSetValidation getCharSetValidation() {
        return new CharSetValidationImpl();
    }

    @Bean
    public EnvironmentReader getEnvironmentReader() {
        return new EnvironmentReaderImpl();
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AccountsDatesHelper getAccountsDatesHelper() {
        return new AccountsDatesHelperImpl();
    }

    @Bean
    public TokenPermissionsInterceptor tokenPermissionsInterceptor() {
        return new TokenPermissionsInterceptor();
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}