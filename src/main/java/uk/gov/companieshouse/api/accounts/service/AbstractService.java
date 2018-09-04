package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

public interface AbstractService<T extends RestObject, U extends BaseEntity> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId, String requestId)
            throws DataException;

    ResponseObject<T> findById(String id);

    void addEtag(T rest);

    void addKind(T rest);

    String getResourceName();

    String generateID(String value) throws NoSuchAlgorithmException;
}