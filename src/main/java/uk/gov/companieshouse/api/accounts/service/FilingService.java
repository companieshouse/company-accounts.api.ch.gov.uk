package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.filing.Filing;

public interface FilingService {

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @param transaction
     * @param accountEntity
     * @return
     */
    Filing generateAccountFiling(String transaction, String accountEntity);
}