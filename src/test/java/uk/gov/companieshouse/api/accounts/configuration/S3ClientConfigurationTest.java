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

import software.amazon.awssdk.services.s3.S3Client;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class S3ClientConfigurationTest {
    @InjectMocks
    private S3ClientConfiguration s3ClientConfiguration;

    @Mock
    private EnvironmentReader environmentReader;

    @BeforeEach
    void setup() {
        when(environmentReader.getMandatoryString("REGION_NAME_FOR_AMAZON_S3")).thenReturn("eu-west-2");
    }

    @Test
    @DisplayName("Test get Amazon S3 without Providing Proxy")
    void testGetAmazonS3WithoutProxy() {
        S3Client result = s3ClientConfiguration.getS3Client();
        assertNotNull(result);
        verifyRegionCheck();
    }

    /**
     * Verify the region configuration check
     */
    private void verifyRegionCheck() {
        verify(environmentReader, times(1)).getMandatoryString("REGION_NAME_FOR_AMAZON_S3");
    }

}
