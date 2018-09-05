package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.api.accounts.validation.Results;
import uk.gov.companieshouse.environment.EnvironmentReader;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TnepValidationServiceImplTest {


    @Mock
    RestTemplate restTemplate;

    @Mock
    EnvironmentReader environmentReader;

    private TnepValidationServiceImpl tnepValidationService;

    @BeforeEach
    public void setup(){
    	MockitoAnnotations.initMocks(this);
        tnepValidationService = new TnepValidationServiceImpl(restTemplate, environmentReader);
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
        		new RestClientException("unit test failure"));

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
