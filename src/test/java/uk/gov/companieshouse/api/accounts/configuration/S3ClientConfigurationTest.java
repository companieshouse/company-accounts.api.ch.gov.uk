package uk.gov.companieshouse.api.accounts.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.s3.S3Client;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ClientConfigurationTest {

    private static final String REGION = "eu-west-2";
    private static final String PROXY_HOST = "PROXY_HOST";
    private static final String PROXY_PORT = "8080";
    private static final String PROXY_PROTOCOL = "HTTPS";

    @Spy
    private ApacheHttpClient.Builder httpClientBuilder;

    @Test
    @DisplayName("Test get Amazon S3 without Providing Proxy")
    void testGetAmazonS3WithoutProxy() {
        S3ClientConfiguration s3NonProxyClientConfiguration = spy(setupNonProxyS3ClientConfiguration());
        S3Client result = s3NonProxyClientConfiguration.getS3Client();
        assertNotNull(result);
        verify(httpClientBuilder, times(1)).build();
        verify(httpClientBuilder, times(0)).proxyConfiguration(any());
    }

    @Test
    @DisplayName("Test get Amazon S3 by Providing Proxy")
    void testGetAmazonS3WithProxy() {
        when(httpClientBuilder.proxyConfiguration(any())).thenReturn(httpClientBuilder);

        S3ClientConfiguration s3ProxyClientConfiguration = spy(setupProxyS3ClientConfiguration());
        S3Client result = s3ProxyClientConfiguration.getS3Client();
        assertNotNull(result);
        verify(httpClientBuilder, times(1)).build();
        verify(httpClientBuilder, times(1)).proxyConfiguration(any());
    }

    private S3ClientConfiguration setupProxyS3ClientConfiguration() {
        return new S3ClientConfiguration(REGION, PROXY_HOST, PROXY_PORT, PROXY_PROTOCOL, httpClientBuilder);
    }

    private S3ClientConfiguration setupNonProxyS3ClientConfiguration() {
        return new S3ClientConfiguration(REGION, null, null, null, httpClientBuilder);
    }
}
