package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
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
    private ResponseEntity response;

    @Mock
    private FilingService filingServiceMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;

    @InjectMocks
    private FilingController filingController;

    @Test
    @DisplayName("Tests the successful creation of the ixbrl - filing is not null")
    public void shouldGenerateFiling() {

        when(filingServiceMock.generateAccountFiling())
            .thenReturn(new Filing());

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of the ixbrl - filing is null")
    void shouldNotGenerateFiling() throws IOException, NoSuchAlgorithmException {

        when(filingServiceMock.generateAccountFiling())
            .thenReturn(null);

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    }
}