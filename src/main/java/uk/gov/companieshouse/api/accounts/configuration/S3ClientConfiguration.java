package uk.gov.companieshouse.api.accounts.configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Configuration
public class S3ClientConfiguration {

    private EnvironmentReader environmentReader;

    public S3ClientConfiguration(EnvironmentReader environmentReader) {
        this.environmentReader = environmentReader;
    }

    @Bean
    public S3Client getS3Client() {
        return S3Client.builder()
            .region(getRegion())
            .build();
    }

    private Region getRegion() {
        return Region.of(getRegionNameForS3Client());
    }


    private String getRegionNameForS3Client() {
        return environmentReader.getMandatoryString("REGION_NAME_FOR_AMAZON_S3");
    }
}
