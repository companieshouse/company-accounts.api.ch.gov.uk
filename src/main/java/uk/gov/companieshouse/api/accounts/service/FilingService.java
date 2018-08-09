package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.filing.Filing;

import java.io.IOException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface FilingService {

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @param transaction - transaction
     * @param accountsId - accounts id
     * @return {@link Filing}
     * @throws IOException
     */
    Filing generateAccountFiling(Transaction transaction, String accountsId) throws IOException;
}