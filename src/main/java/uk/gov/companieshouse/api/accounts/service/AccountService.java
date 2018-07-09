package uk.gov.companieshouse.api.accounts.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;

/**
 * A service for {@link AccountEntity} and its data {@link AccountDataEntity}
 */
public interface AccountService {

    /**
     * Create the {@link AccountEntity} that will be persisted to the DB
     * from the given {@link Account} object and returns {@link ResponseEntity}
     * detailing its success or failure
     *
     * @return An {@link ResponseEntity}
     */
    Account createAccount(Account account);
}