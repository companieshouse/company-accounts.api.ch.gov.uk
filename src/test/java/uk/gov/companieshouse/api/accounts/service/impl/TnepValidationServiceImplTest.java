package uk.gov.companieshouse.api.accounts.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.service.TnepValidationService;
import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.environment.EnvironmentReader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class TnepValidationServiceImplTest {


    @Mock
    RestTemplate restTemplate;

    @Mock
    EnvironmentReader environmentReader;


    private TnepValidationServiceImpl tnepValidationService;

    @BeforeEach
    public void setup(){
    	MockitoAnnotations.initMocks(this);
        tnepValidationService = new TnepValidationServiceImpl();
    }

    @Test
    public void validationSuccess() throws IOException {

        Results results = new Results();
        results.setValidationStatus("OK");

        when(environmentReader.getMandatoryString(anyString())).thenReturn("testuri");
        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class))).thenReturn(results);



        assertTrue(validateIxbrl());

    }

    @Test
    public void validationFailure() throws IOException {

        Results results = new Results();
        results.setValidationStatus("unit test failure");

        when(environmentReader.getMandatoryString(anyString())).thenReturn("testuri");
        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class))).thenReturn(results);


        assertFalse(validateIxbrl());

    }

    @Test
    public void validationMissingResponse() throws IOException {

        when(environmentReader.getMandatoryString(anyString())).thenReturn("testuri");
        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class))).thenReturn(null);


        assertFalse(validateIxbrl());

    }

    @Test
    public void invalidResponse() {

        when(environmentReader.getMandatoryString(anyString())).thenReturn("testuri");
        when(restTemplate.postForObject(any(URI.class), any(HttpEntity.class), eq(Results.class))).thenThrow(
            new Exception());

        assertFalse(validateIxbrl());


    }

    @Test
    public void missingEnvVariable() {

        when(environmentReader.getMandatoryString(anyString())).thenReturn(null);

        assertFalse(validateIxbrl());
    }

    private boolean validateIxbrl() {
        return tnepValidationService.validate("test", "S3-LOCATION");
    }
}
