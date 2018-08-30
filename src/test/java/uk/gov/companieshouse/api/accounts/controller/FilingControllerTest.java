package uk.gov.companieshouse.api.accounts.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FilingControllerTest {

    private ResponseEntity response;


    private FilingController filingController = new FilingController();

    @Test
    @DisplayName("Tests the filing controller successful response")
    public void shouldGenerateFiling() {
        response = filingController.generateFiling();
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }
}