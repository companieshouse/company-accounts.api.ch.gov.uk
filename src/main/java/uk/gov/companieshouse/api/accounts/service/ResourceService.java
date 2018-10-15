package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface ResourceService<T extends RestObject> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
        String requestId)
        throws DataException;

    ResponseObject<T> update(T rest, Transaction transaction, String companyAccountId,
        String requestId)
        throws DataException;

    ResponseObject<T> findById(String id, String requestId)
        throws DataException;

    String generateID(String companyAccountId);

}