package uk.gov.companieshouse.api.accounts;

import com.mongodb.MongoClientOptions;

import uk.gov.companieshouse.accountsDates.AccountsDates;
import uk.gov.companieshouse.accountsDates.impl.AccountsDatesImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

/**
 * General application configuration .
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public EnvironmentReader environmentReader() {
        return new EnvironmentReaderImpl();
    }
    
    @Bean
    public AccountsDates accountsDates() {
        return new AccountsDatesImpl();
    }
}
