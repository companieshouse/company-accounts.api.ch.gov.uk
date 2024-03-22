package uk.gov.companieshouse.api.accounts.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.environment.EnvironmentReader;
import com.amazonaws.services.s3.AmazonS3;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AmazonS3ConfigurationTest {

    @InjectMocks
    private AmazonS3Configuration amazonS3Configuration;

    @Mock
    private EnvironmentReader environmentReader;

    @BeforeEach
    void setup() {
        when(environmentReader.getMandatoryString("REGION_NAME_FOR_AMAZON_S3")).thenReturn("eu-west-2");
    }

    @Test
    @DisplayName("Test get Amazon S3 without Providing Proxy")
    void testGetAmazonS3WithoutProxy() {
        AmazonS3 result = amazonS3Configuration.getAmazonS3();
        assertNotNull(result);
        verifyProxyCheck();
        verifyRegionCheck();
    }

    @Test
    @DisplayName("Test get Amazon S3 by Providing Proxy")
    void testGetAmazonS3WithProxy() {
        when(environmentReader.getOptionalInteger("HTTP_URL_CONNECTION_PROXY_PORT")).thenReturn(8080);
        when(environmentReader.getOptionalString("IMAGE_CLOUD_PROXY_HOST")).thenReturn("PROXY_HOST");
        AmazonS3 result = amazonS3Configuration.getAmazonS3();
        assertNotNull(result);
        verifyProxyCheck();
        verifyRegionCheck();
    }

    /**
     * Verify the proxy configuration check
     */
    private void verifyProxyCheck() {
        verify(environmentReader, times(1)).getOptionalString("IMAGE_CLOUD_PROXY_HOST");
        verify(environmentReader, times(1)).getOptionalInteger("HTTP_URL_CONNECTION_PROXY_PORT");
    }

    /**
     * Verify the region configuration check
     */
    private void verifyRegionCheck() {
        verify(environmentReader, times(1)).getMandatoryString("REGION_NAME_FOR_AMAZON_S3");
    }

}
