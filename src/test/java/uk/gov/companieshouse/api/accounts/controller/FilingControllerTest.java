package uk.gov.companieshouse.api.accounts.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@TestInstance(Lifecycle.PER_CLASS)
public class FilingControllerTest {

    private FilingController filingController = new FilingController();

    @Test
    @DisplayName("Tests the filing controller successful response")
    public void shouldGenerateFiling() {
        ResponseEntity response = filingController.generateFiling();
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }
}