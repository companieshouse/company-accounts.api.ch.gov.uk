package uk.gov.companieshouse.api.accounts.service;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface ParentService<T extends RestObject, U extends LinkType> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
            HttpServletRequest request)
            throws DataException;

    ResponseObject<T> find(String companyAccountsId, HttpServletRequest request)
            throws DataException;

    ResponseObject<T> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException;

    void addLink(String id, U linkType, String link, HttpServletRequest request)
        throws DataException;

    void removeLink(String id, U linkType, HttpServletRequest request) throws DataException;

}