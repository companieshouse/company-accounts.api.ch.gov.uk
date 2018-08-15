package uk.gov.companieshouse.api.accounts;

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

}