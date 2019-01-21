package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.stereotype.Component;

@Component
public class AmazonS3HandlerImpl implements AmazonS3Handler {

    /**
     * {@inheritDoc}
     */
    @Override
    public AmazonS3 getAmazonS3(String awsAccessKeyId, String awsSecretAccessKey,
        String cloudProxyHost, Integer cloudProxyPort) {

        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);

        ClientConfiguration clientConfiguration =
            getClientConfiguration(cloudProxyHost, cloudProxyPort);

        return new AmazonS3Client(credentials, clientConfiguration);
    }

    /**
     * Create ClientConfiguration with the Proxy Host and  Proxy port (if they have passed in).
     *
     * @return A {@link ClientConfiguration}
     */
    private ClientConfiguration getClientConfiguration(String cloudProxyHost,
        Integer cloudProxyPort) {

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
