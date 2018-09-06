package uk.gov.companieshouse.api.accounts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

/**
 * General application configuration .
 */
@Configuration
public class ApplicationConfiguration {

    @Bean
    public MessageDigest getMessageDigest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
    }
    
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public EnvironmentReader getEnvironmentReader() {
        return new EnvironmentReaderImpl();
    }
}