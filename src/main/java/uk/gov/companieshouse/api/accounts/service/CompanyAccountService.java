package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

/**
 * A service for {@link CompanyAccountEntity} and its data {@link CompanyAccountDataEntity}
 */
public interface CompanyAccountService {

    ResponseObject<CompanyAccount> create(CompanyAccount companyAccount,
        Transaction transaction,
        String requestId) throws PatchException, DataException;

    ResponseObject<CompanyAccount> findById(String id, String requestId)
        throws DataException;

    void addLink(String id, LinkType linkType, String link);

}