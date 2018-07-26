package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

/**
 * A service for {@link CompanyAccountEntity} and its data {@link CompanyAccountDataEntity}
 */
public interface CompanyAccountService extends AbstractService<CompanyAccount, CompanyAccountEntity>{

    CompanyAccount createCompanyAccount(CompanyAccount companyAccount);

}