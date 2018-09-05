package uk.gov.companieshouse.api.accounts.service.impl;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

class FilingServiceImplTest {

    private final FilingService filingService = new FilingServiceImpl();

    @Mock
    private Transaction transactionMock;
    @Mock
    private CompanyAccountEntity companyAccountEntityMock;

    @Test
    @DisplayName("Tests the filing generation. Happy path")
    void shouldGenerateFiling() {
        Filing filing = filingService
            .generateAccountFiling(transactionMock, companyAccountEntityMock);

        assertNotNull(filing);
    }
}