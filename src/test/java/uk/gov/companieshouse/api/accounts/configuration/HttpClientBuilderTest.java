package uk.gov.companieshouse.api.accounts.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;


class HttpClientBuilderTest {
    
    @Test
    void testGetApacheHttpClientBuilder() {
        HttpClientBuilder httpClientBuilder = new HttpClientBuilder();
        assertNotNull(httpClientBuilder.getApacheHttpClientBuilder());
    }
}
