package uk.gov.companieshouse.api.accounts;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.concurrent.TimeUnit;


/**
 * Custom configurations for Mongo
 *
 * 1. MappingMongoConverter that doesn't save _class to mongo.
 * 2. MongoClientSettings that externalises the connection pooling options
 */
@Configuration
public class MongoConfig {

    /**
     * _class maps to the model class in mongoDB (i.e. _class : uk.gov.companieshouse.Transaction)
     * when using spring data mongo it by default adds a _class key to your collection to be able to
     * handle inheritance. But if your domain model is simple and flat, you can remove it by overriding
     * the default MappingMongoConverter
     *
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

    /**
     * Create a {@link MongoClientSettings} .
     *
     * @return A {@link MongoClientSettings} .
     */
    @Bean
    public MongoClientSettings mongoClientSettings(MongoDbConnectionPoolConfig connectionPoolConfig) {

        ConnectionString connectionString = new ConnectionString(connectionPoolConfig.getConnectionString());

        return MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> builder.minSize(connectionPoolConfig.getMinSize())
                        .maxConnectionIdleTime(connectionPoolConfig.getMaxConnectionIdleTimeMS(), TimeUnit.MILLISECONDS)
                        .maxConnectionLifeTime(connectionPoolConfig.getMaxConnectionLifeTimeMS(), TimeUnit.MILLISECONDS)).build();
    }
}