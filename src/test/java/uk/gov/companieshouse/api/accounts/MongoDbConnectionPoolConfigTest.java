package uk.gov.companieshouse.api.accounts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoDbConnectionPoolConfigTest {

    private MongoDbConnectionPoolConfig mongoDbConnectionPoolConfig;

    @BeforeEach
    void setup() {
        mongoDbConnectionPoolConfig = new MongoDbConnectionPoolConfig();
    }

    @Test
    @DisplayName("Test mongo db connection pool config settings correctly default to set values")
    void assertValuesDefaultCorrectly() {
        String DEFAULT_URL = "mongodb://mongo:27017";
        assertEquals(DEFAULT_URL, mongoDbConnectionPoolConfig.getConnectionString());
        int DEFAULT_MIN_SIZE = 1;
        assertEquals(DEFAULT_MIN_SIZE, mongoDbConnectionPoolConfig.getMinSize());
        int DEFAULT_MAX_CONNECTION_IDLE_TIME = 0;
        assertEquals(DEFAULT_MAX_CONNECTION_IDLE_TIME, mongoDbConnectionPoolConfig.getMaxConnectionIdleTimeMS());
        int DEFAULT_MAX_CONNECTION_LIFE_TIME = 0;
        assertEquals(DEFAULT_MAX_CONNECTION_LIFE_TIME, mongoDbConnectionPoolConfig.getMaxConnectionLifeTimeMS());
    }
}
