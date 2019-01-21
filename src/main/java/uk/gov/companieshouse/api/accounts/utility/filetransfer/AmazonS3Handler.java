package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import com.amazonaws.services.s3.AmazonS3;

public interface AmazonS3Handler {

    /**
     * Create AmazonS3 using the access key and secret access key. This will be configured to use a
     * proxy when the port and host parameters are not null.
     *
     * @param awsAccessKeyId - Aws key of the environment
     * @param awsSecretAccessKey - Aws secret access key
     * @param cloudProxyHost - Host of the proxy to be used when creating the ClientConfiguration
     * @param cloudProxyPort - Port of the proxy to be used when creating the ClientConfiguration
     *
     * @return {@link AmazonS3}
     */
    AmazonS3 getAmazonS3(String awsAccessKeyId, String awsSecretAccessKey,
        String cloudProxyHost, Integer cloudProxyPort);
}
