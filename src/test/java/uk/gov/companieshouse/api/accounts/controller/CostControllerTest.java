package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Cost;
import uk.gov.companieshouse.api.accounts.service.CostService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CostControllerTest {

    @Mock
    private CostService costService;

    @InjectMocks
    private CostController costController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @BeforeEach
    private void setUp() {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue()))
                .thenReturn(transaction);
    }

    @Test
    @DisplayName("Get costs for payable transaction")
    void getCostsForPayableTransaction() throws DataException {

        Cost[] costs = {new Cost()};

        when(costService.getCosts(transaction)).thenReturn(costs);

        ResponseEntity<Cost[]> response = costController.get(COMPANY_ACCOUNTS_ID, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(costs, response.getBody());
    }

    @Test
    @DisplayName("Get costs for non-payable transaction")
    void getCostsForNonPayableTransaction() throws DataException {

        Cost[] costs = {};

        when(costService.getCosts(transaction)).thenReturn(costs);

        ResponseEntity<Cost[]> response = costController.get(COMPANY_ACCOUNTS_ID, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get costs - data exception")
    void getCostsDataException() throws DataException {

        when(costService.getCosts(transaction)).thenThrow(DataException.class);

        ResponseEntity<Cost[]> response = costController.get(COMPANY_ACCOUNTS_ID, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}
