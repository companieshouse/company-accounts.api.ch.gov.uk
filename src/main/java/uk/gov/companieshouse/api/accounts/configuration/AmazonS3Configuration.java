package uk.gov.companieshouse.api.accounts.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Configuration {

    @Value("${aws.configs.cloud.host}")
    private String cloudProxyHost;

    @Value("${aws.configs.cloud.port}")
    private Integer cloudProxyPort;

    @Bean
    public AmazonS3 getAmazonS3() {
        return AmazonS3ClientBuilder
            .standard()
            .withRegion(Regions.EU_WEST_1)
            .withClientConfiguration(getClientConfiguration())
            .build();
    }

    /**
     * Create ClientConfiguration with the Proxy Host and  Proxy port (if they have passed in).
     *
     * @return A {@link ClientConfiguration}
     */
    private ClientConfiguration getClientConfiguration() {

        ClientConfiguration clientConfiguration = new ClientConfiguration();

        if (cloudProxyHost != null && !cloudProxyHost.trim().isEmpty()) {
            clientConfiguration.setProxyHost(cloudProxyHost);
        }

        if (cloudProxyPort != null) {
            clientConfiguration.setProxyPort(cloudProxyPort);
        }
        clientConfiguration.setProtocol(Protocol.HTTPS);

        return clientConfiguration;
    }
}
