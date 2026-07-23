package uk.gov.companieshouse.api.accounts.configuration;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;

import java.net.URI;

@Configuration
public class S3ClientConfiguration {

    private String region;
    private String endpoint;
    private String port;
    private String protocol;
    private final ApacheHttpClient.Builder httpClientBuilder;

    public S3ClientConfiguration(
        @Value("${cloud.aws.s3.region}") String region,
        @Value("${cloud.aws.s3.proxy.endpoint}") String endpoint,
        @Value("${cloud.aws.s3.proxy.port}") String port,
        @Value("${cloud.aws.s3.proxy.protocol}") String protocol,
        ApacheHttpClient.Builder httpClientBuilder
                                ) {
        this.region = region;
        this.endpoint = endpoint;
        this.port = port;
        this.protocol = protocol;
        this.httpClientBuilder = httpClientBuilder;
    }

    @Bean
    public S3Client getS3Client() {
        return S3Client.builder()
            .region(getRegion())
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClient(getSdkHttpClient(getProxyHost(), getProxyPort(), getProxyProtocol()))
            .build();
    }

    private Region getRegion() {
        return Region.of(getRegionNameForAmazonS3());
    }


    private String getRegionNameForAmazonS3() {
        return region;
    }

    private String getProxyPort() {
        return port;
    }

    private String getProxyHost() {
        return endpoint;
    }

    private String getProxyProtocol() {
        return protocol;
    }

    private SdkHttpClient getSdkHttpClient(String proxyHost, String proxyPort, String proxyProtocol) {

        if (proxyHost == null || proxyHost.isEmpty()) {
            return httpClientBuilder
                .build();
        }

        String proxyEndpoint = proxyProtocol + "://" + proxyHost + ":" + proxyPort;

        ProxyConfiguration proxyConfiguration = ProxyConfiguration.builder()
            .endpoint(URI.create(proxyEndpoint))
            .build();

        return httpClientBuilder
            .proxyConfiguration(proxyConfiguration)
            .build();
    }
}
