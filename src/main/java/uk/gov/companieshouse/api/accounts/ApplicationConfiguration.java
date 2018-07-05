package uk.gov.companieshouse.api.accounts;

import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.companieshouse.charset.validation.CharSetValidation;
import uk.gov.companieshouse.charset.validation.impl.CharSetValidationImpl;


/**
 * General application configuration .
 */
@Configuration
@SuppressWarnings("unused")
public class ApplicationConfiguration {

    @Autowired
    private MongoDbConnectionPoolProperties configuration;

    /**
     * Create a {@link MongoClientOptions} .
     *
     * @return A {@link MongoClientOptions} .
     */
    @Bean
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder()
                .minConnectionsPerHost(configuration.getMinSize())
                .maxConnectionIdleTime(configuration.getMaxConnectionIdleTimeMS())
                .maxConnectionLifeTime(configuration.getMaxConnectionLifeTimeMS())
                .build();
    }
}