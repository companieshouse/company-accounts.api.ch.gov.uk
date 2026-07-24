package uk.gov.companieshouse.api.accounts.configuration;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache5.Apache5HttpClient;
import software.amazon.awssdk.http.apache5.ProxyConfiguration;

import java.net.URI;

@Configuration
public class S3ClientConfiguration {

    private String region;
    private String host;
    private String port;
    private String protocol;
    private final Apache5HttpClient.Builder httpClientBuilder;

    public S3ClientConfiguration(
        @Value("${cloud.aws.s3.region}") String region,
        @Value("${cloud.aws.s3.proxy.host}") String host,
        @Value("${cloud.aws.s3.proxy.port}") String port,
        @Value("${cloud.aws.s3.proxy.protocol}") String protocol,
        Apache5HttpClient.Builder httpClientBuilder
                                ) {
        this.region = region;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
        this.httpClientBuilder = httpClientBuilder;
    }

    @Bean
    public S3Client getS3Client() {
        if (getProxyHost() == null || getProxyHost().isEmpty()) {
            return getNoProxyS3Client();
        }
        return getProxyS3Client();
    }

    private S3Client getNoProxyS3Client() {
        return S3Client.builder()
            .region(getRegion())
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    private S3Client getProxyS3Client() {
        return S3Client.builder()
            .region(getRegion())
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClientBuilder(getSdkHttpClientBuilder(getProxyHost(), getProxyPort(), getProxyProtocol()))
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
        return host;
    }

    private String getProxyProtocol() {
        return protocol;
    }

    private SdkHttpClient.Builder<Apache5HttpClient.Builder> getSdkHttpClientBuilder(String proxyHost, String proxyPort, String proxyProtocol) {

        String proxyEndpoint = proxyProtocol + "://" + proxyHost + ":" + proxyPort;

        ProxyConfiguration proxyConfiguration = ProxyConfiguration.builder()
            .endpoint(URI.create(proxyEndpoint))
            .build();

        return httpClientBuilder
            .proxyConfiguration(proxyConfiguration);
    }
}
