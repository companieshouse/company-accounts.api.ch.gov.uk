package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.entity.AccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;

/**
 * A service for {@link AccountEntity} and its data {@link AccountDataEntity}
 */
public interface AccountService {

    /**
     * Create the {@link AccountEntity} that will be mapped from the rest request, ready for
     * persistence to the database.
     *
     * @return A mapped {@link Account} object from the data inserted to the database
     */
    Account createAccount(Account account);
}