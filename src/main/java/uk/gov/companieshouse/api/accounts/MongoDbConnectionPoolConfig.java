package uk.gov.companieshouse.api.accounts;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Component
public class MongoDbConnectionPoolConfig {

    private Integer minSize;
    private int maxConnectionIdleTimeMS;
    private int maxConnectionLifeTimeMS;

    /**
     * Constructs the config using environment variables for
     * Mongo Connection Pool settings. Sets default values in case
     * the environment variables are not supplied.
     */
    public MongoDbConnectionPoolConfig() {
        EnvironmentReader reader = new EnvironmentReaderImpl();

        this.minSize = reader.getOptionalInteger("MONGO_CONNECTION_POOL_MIN_SIZE_KEY") != null ? this.minSize : 1;

        this.maxConnectionIdleTimeMS = reader.getOptionalInteger("MONGO_CONNECTION_MAX_IDLE_KEY") != null ? this.maxConnectionIdleTimeMS : 0;
        this.maxConnectionLifeTimeMS = reader.getOptionalInteger("MONGO_CONNECTION_MAX_LIFE_KEY") != null ? this.maxConnectionLifeTimeMS : 0;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxConnectionIdleTimeMS() {
        return maxConnectionIdleTimeMS;
    }

    public int getMaxConnectionLifeTimeMS() {
        return maxConnectionLifeTimeMS;
    }
}