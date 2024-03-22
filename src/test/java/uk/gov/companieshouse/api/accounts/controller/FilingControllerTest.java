package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FilingControllerTest {

    private static final String TRANSACTION_ID = "1234561-1234561-1234561";
    private static final String ACCOUNTS_ID = "1234561";
    private ResponseEntity response;

    @Mock
    private FilingService filingServiceMock;
    @Mock
    private HttpServletRequest httpServletRequestMock;
    @Mock
    private Transaction transactionMock;
    @Mock
    private CompanyAccount companyAccount;

    @InjectMocks
    private FilingController filingController;

    @Test
    @DisplayName("Tests the successful creation of the ixbrl - filing is not null")
    void shouldGenerateFiling() {
        mockHttpServletRequestAllAttributesSet();

        when(filingServiceMock.generateAccountFiling(transactionMock, companyAccount))
            .thenReturn(new Filing());

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of the ixbrl - filing is null")
    void shouldNotGenerateFiling() {

        mockHttpServletRequestAllAttributesSet();
        when(filingServiceMock.generateAccountFiling(transactionMock, companyAccount))
            .thenReturn(null);

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    }

    @Test
    @DisplayName("Tests the transaction not being set in the request's attribute")
    void shouldFailTransactionAsNotSetInRequest() {

        when(httpServletRequestMock.getAttribute(anyString())).thenReturn(null);

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    }

    @Test
    @DisplayName("Tests the company account not being set in the request's attribute")
    void shouldFailAsCompanyAccountNotSetInRequest() {

        when(httpServletRequestMock.getAttribute(anyString()))
            .thenReturn(transactionMock)
            .thenReturn(null);

        response =
            filingController.generateFiling(TRANSACTION_ID, ACCOUNTS_ID, httpServletRequestMock);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode().value());
    }

    private void mockHttpServletRequestAllAttributesSet() {
        when(httpServletRequestMock.getAttribute(anyString()))
            .thenReturn(transactionMock)
            .thenReturn(companyAccount);
    }
}