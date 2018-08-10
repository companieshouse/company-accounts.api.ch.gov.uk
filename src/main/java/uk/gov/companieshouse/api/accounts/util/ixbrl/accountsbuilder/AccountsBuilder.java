package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder;

import java.io.IOException;

/**
 * Interface to call Accounts API to retrieve the accounts information.
 *
 * This functionality has not been implemented yet. Waiting for the API changes to be deployed;
 * once, that happens the accounts builder implementation needs to be change.
 **/
public interface AccountsBuilder {
    Object buildAccount() throws IOException;
}
