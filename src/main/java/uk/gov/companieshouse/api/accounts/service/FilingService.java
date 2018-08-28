package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;

import java.io.IOException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface FilingService {

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @param transaction - transaction
     * @param accountEntity - accounts information
     * @return {@link Filing}
     * @throws IOException
     */
    Filing generateAccountFiling(Transaction transaction, CompanyAccountEntity accountEntity)
        throws IOException, NoSuchAlgorithmException;
}