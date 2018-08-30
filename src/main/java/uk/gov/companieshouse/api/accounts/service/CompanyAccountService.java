package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.transaction.ApiErrorResponseException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

/**
 * A service for {@link CompanyAccountEntity} and its data {@link CompanyAccountDataEntity}
 */
public interface CompanyAccountService {

    CompanyAccount createCompanyAccount(CompanyAccount companyAccount, Transaction transaction,
            String requestId) throws ApiErrorResponseException, DataException;

    CompanyAccountEntity findById(String id);

}