package uk.gov.companieshouse.api.accounts.service;

import java.io.IOException;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;

public interface FilingService {

    /**
     * Generate a filing with the ixbrl location that is generated for transaction and accounts id.
     *
     * @param transaction - transaction
     * @param accountEntity - accounts information
     * @return {@link Filing}
     * @throws IOException
     */
    Filing generateAccountFiling(String transaction, String accountEntity);
}