package uk.gov.companieshouse.api.accounts;

import com.mongodb.MongoClientOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * Custom configurations for Mongo
 *
 * 1. MappingMongoConverter that doesn't save _class to mongo.
 * 2. MongoClientOptions that externalises the connection pooling options
 */
@Configuration
public class MongoConfig {

    @Autowired
    private MongoDbConnectionPoolProperties configuration;

    /**
     * _class maps to the model class in mongoDB (i.e. _class : uk.gov.companieshouse.Transaction)
     * when using spring data mongo it by default adds a _class key to your collection to be able to
     * handle inheritance. But if your domain model is simple and flat, you can remove it by overriding
     * the default MappingMongoConverter
     *
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

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