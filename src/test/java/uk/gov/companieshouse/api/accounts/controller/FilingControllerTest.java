package uk.gov.companieshouse.api.accounts.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FilingControllerTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private static final String COMPANY_NUMBER = "1234";
    private ResponseEntity response;

    @Mock
    private FilingService filingServiceMock;
        @InjectMocks
    private FilingController filingController;

    @Test
    public void shouldGenerateFiling() throws IOException {
        when(filingServiceMock.generateAccountFiling(anyString(), anyString()))
            .thenReturn(createFiling(false));

        response = filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldNotGenerateFilingInternalErrorResponse() throws IOException {
        when(filingServiceMock.generateAccountFiling(anyString(), anyString()))
            .thenReturn(createFiling(true));

        response = filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    private Filing createFiling(boolean createEmptyObject) {
        Filing filing = null;
        if (!createEmptyObject) {
            filing = new Filing();
            filing.setCompanyNumber(COMPANY_NUMBER);
        }
        return filing;
    }
}
