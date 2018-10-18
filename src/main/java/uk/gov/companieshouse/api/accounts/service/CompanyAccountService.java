package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
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
        Transaction transaction, HttpServletRequest request) throws PatchException, DataException;

    ResponseObject<CompanyAccount> findById(String id, HttpServletRequest request)
        throws DataException;

    void addLink(String id, CompanyAccountLinkType linkType, String link);

}