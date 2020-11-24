package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceImplTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private TransactionService transactionService = new TransactionServiceImpl();

    @Mock
    private Transaction transaction;

    @Test
    @DisplayName("Get payable resources - is CIC")
    void getPayableResourcesForCIC() throws ServiceException {

        when(companyService.isCIC(transaction)).thenReturn(true);

        List<PayableResource> payableResources = transactionService.getPayableResources(transaction);

        assertTrue(payableResources.contains(PayableResource.CIC));
    }

    @Test
    @DisplayName("Get payable resources - none present")
    void getPayableResourcesNonePresent() throws ServiceException {

        when(companyService.isCIC(transaction)).thenReturn(false);

        List<PayableResource> payableResources = transactionService.getPayableResources(transaction);

        assertTrue(payableResources.isEmpty());
    }
}
