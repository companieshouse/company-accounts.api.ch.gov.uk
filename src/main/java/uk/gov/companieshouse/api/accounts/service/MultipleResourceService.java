package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface MultipleResourceService<T extends RestObject> extends ResourceService<T> {

    ResponseObject<T> findAll(Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException;

    ResponseObject<T> deleteAll(Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException;
}
