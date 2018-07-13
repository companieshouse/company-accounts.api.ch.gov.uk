package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

/**
 * A service for {@link CompanyAccountEntity} and its data {@link CompanyAccountDataEntity}
 */
public interface CompanyAccountService {

    /**
     * Create the {@link CompanyAccountEntity} that will be mapped from the rest request, ready for
     * persistence to the database.
     *
     * @return A mapped {@link CompanyAccount} object from the data inserted to the database
     */
    CompanyAccount createCompanyAccount(CompanyAccount companyAccount);
}