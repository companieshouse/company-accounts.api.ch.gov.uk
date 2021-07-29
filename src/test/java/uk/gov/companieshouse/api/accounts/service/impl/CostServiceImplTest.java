package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.Costs;
import uk.gov.companieshouse.api.accounts.service.CostService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.utility.YamlResourceMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CostServiceImplTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private YamlResourceMapper yamlResourceMapper;

    private CostService costService;

    @Mock
    private Transaction transaction;

    @Mock
    private Cost cost;

    @Mock
    private Costs costs;

    @Mock
    private Map<String, Cost> costMap;

    @BeforeEach
    private void setUp() {

        when(yamlResourceMapper.fetchObjectFromYaml("costs/costs.yaml", Costs.class))
                .thenReturn(costs);

        this.costService = new CostServiceImpl(transactionService, yamlResourceMapper);
    }

    @Test
    @DisplayName("Get costs for payable transaction")
    void getCostsForPayableTransaction() throws ServiceException, DataException {

        when(transactionService.getPayableResources(transaction))
                .thenReturn(Arrays.asList(PayableResource.CIC));

        when(costs.getCostsMap()).thenReturn(costMap);

        when(costMap.get(PayableResource.CIC.getResource())).thenReturn(cost);

        Cost[] costArray = costService.getCosts(transaction);

        assertEquals(1, costArray.length);
        assertEquals(cost, costArray[0]);
    }

    @Test
    @DisplayName("Get costs for non-payable transaction")
    void getCostsForNonPayableTransaction() throws ServiceException, DataException {

        when(transactionService.getPayableResources(transaction))
                .thenReturn(new ArrayList<>());

        Cost[] costArray = costService.getCosts(transaction);

        assertEquals(0, costArray.length);
    }

    @Test
    @DisplayName("Get costs - transaction service exception")
    void getCostsTransactionServiceException() throws ServiceException {

        when(transactionService.getPayableResources(transaction))
                .thenThrow(ServiceException.class);

        assertThrows(DataException.class, () -> costService.getCosts(transaction));
    }
}
