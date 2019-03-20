package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface ResourceService<T extends RestObject> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
        HttpServletRequest request)
        throws DataException;

    ResponseObject<T> update(T rest, Transaction transaction, String companyAccountId,
        HttpServletRequest request)
        throws DataException;

    ResponseObject<T> find(String companyAccountsId, HttpServletRequest request)
        throws DataException;

    ResponseObject<T> delete(String companyAccountsId, HttpServletRequest request)
        throws DataException;

}