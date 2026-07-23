package uk.gov.companieshouse.api.accounts.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.http.apache.ApacheHttpClient;

@Configuration
public class HttpClientBuilder {
    
    @Bean
    public ApacheHttpClient.Builder getApacheHttpClientBuilder() {
        return ApacheHttpClient.builder();
    }

}
