package uk.gov.companieshouse.api.accounts;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.environment.EnvironmentReader;

class ApplicationConfigurationTest {
	private ApplicationConfiguration applicationConfiguration;
	
	@BeforeEach
	void setUp() {
		applicationConfiguration = new ApplicationConfiguration();
	}
	
	@Test
	@DisplayName("Get the bean for sending REST requests")
	void getBeanForRestRequests() {
		RestTemplate bean = applicationConfiguration.getRestTemplate();
		assertNotNull(bean);
	}
	
	@Test
	@DisplayName("Get the bean for reading environment variables")
	void getBeanForReadingEnvironmentVariables() {
		EnvironmentReader bean = applicationConfiguration.getEnvironmentReader();
		assertNotNull(bean);
	}
}
