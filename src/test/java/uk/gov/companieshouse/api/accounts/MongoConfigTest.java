package uk.gov.companieshouse.api.accounts;

import com.mongodb.MongoClientSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoConfigTest {


    @Mock
    MongoDatabaseFactory mongoDatabaseFactory;

    @Mock
    MongoMappingContext context;

    @Mock
    MongoDbConnectionPoolConfig mongoDbConnectionPoolConfig;

    private MongoConfig mongoConfig;

    private static final String CONNECTION_STRING_TEST = "mongodb://test";

    @BeforeEach
    void setup() {
        mongoConfig = new MongoConfig();
    }

    @Test
    @DisplayName("Get the bean for mapping mongo converter")
    void getBeanForMappingMongoConverter() {
        MappingMongoConverter bean = mongoConfig.mappingMongoConverter(mongoDatabaseFactory, context);
        assertNotNull(bean);
    }

    @Test
    @DisplayName("Get the bean for mongo client settings")
    void getBeanForMongoClientSettings() {

        when(mongoDbConnectionPoolConfig.getConnectionString()).thenReturn(CONNECTION_STRING_TEST);

        MongoClientSettings bean = mongoConfig.mongoClientSettings(mongoDbConnectionPoolConfig);
        assertNotNull(bean);
    }
}
