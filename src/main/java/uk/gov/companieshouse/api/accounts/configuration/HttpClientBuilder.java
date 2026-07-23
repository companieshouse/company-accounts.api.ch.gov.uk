package uk.gov.companieshouse.api.accounts.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.http.apache5.Apache5HttpClient;


@Configuration
public class HttpClientBuilder {
    
    @Bean
    public Apache5HttpClient.Builder getApacheHttpClientBuilder() {
        return Apache5HttpClient.builder();
    }

}
