package uk.gov.companieshouse.api.accounts.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDBEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDataDBEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;

/**
 * A service for {@link AccountsDBEntity} and its data {@link AccountsDataDBEntity}
 */
public interface AccountsService {

    /**
     * Create the {@link AccountsDBEntity} that will be persisted to the DB
     * from the given {@link Accounts} object and returns {@link ResponseEntity}
     * detailing its success or failure
     *
     * @return An {@link ResponseEntity}
     */
    ResponseEntity createAccount(Accounts accounts);
}