package uk.gov.companieshouse.api.accounts;

import java.util.Optional;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Component
public class MongoDbConnectionPoolConfig {

    private Integer minSize;
    private int maxConnectionIdleTimeMS;
    private int maxConnectionLifeTimeMS;
    private String host;
    private int port;


    /**
     * Constructs the config using environment variables for Mongo Connection Pool settings. Sets
     * default values in case the environment variables are not supplied.
     */
    public MongoDbConnectionPoolConfig() {
        EnvironmentReader reader = new EnvironmentReaderImpl();

        this.minSize =
                Optional.ofNullable(reader.getOptionalInteger("MONGO_CONNECTION_POOL_MIN_SIZE_KEY"))
                        .orElse(1);
        this.maxConnectionIdleTimeMS =
                Optional.ofNullable(reader.getOptionalInteger("MONGO_CONNECTION_MAX_IDLE_KEY"))
                        .orElse(0);
        this.maxConnectionLifeTimeMS =
                Optional.ofNullable(reader.getOptionalInteger("MONGO_CONNECTION_MAX_LIFE_KEY"))
                        .orElse(0);
        this.host = Optional.ofNullable(reader.getOptionalString("MONGODB_HOST"))
                .orElse("localhost");
        this.port =
                Optional.ofNullable(reader.getOptionalInteger("MONGODB_PORT"))
                        .orElse(27017);
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

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}