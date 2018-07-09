package uk.gov.companieshouse.api.accounts.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDataEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;

/**
 * A service for {@link AccountsEntity} and its data {@link AccountsDataEntity}
 */
public interface AccountsService {

    /**
     * Create the {@link AccountsEntity} that will be persisted to the DB
     * from the given {@link Accounts} object and returns {@link ResponseEntity}
     * detailing its success or failure
     *
     * @return An {@link ResponseEntity}
     */
    ResponseEntity createAccount(Accounts accounts);
}