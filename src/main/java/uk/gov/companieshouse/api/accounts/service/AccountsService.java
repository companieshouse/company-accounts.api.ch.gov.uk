package uk.gov.companieshouse.api.accounts.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDBEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDataDBEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;

/**
 * A service for {@link AccountsDBEntity}
 */
public interface AccountsService {

    /**
     * Create the given {@link AccountsDataDBEntity}
     *
     * @return An {@link AccountsDataDBEntity}
     */
    ResponseEntity createAccount(Accounts accounts);
}