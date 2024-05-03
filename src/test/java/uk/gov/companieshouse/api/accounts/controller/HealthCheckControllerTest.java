package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {

    @InjectMocks
    private HealthCheckController healthCheckController;

    @Test
    void healthCheckEndpoint_Success() {
        ResponseEntity<String> responseEntity = healthCheckController.healthcheck();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
