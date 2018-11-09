package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface FilingService {

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @param transaction - Transaction information
     * @param companyAccountEntity - Company Account information
     * @return {@link Filing}
     */
    Filing generateAccountFiling(Transaction transaction,
        CompanyAccount companyAccount);
}