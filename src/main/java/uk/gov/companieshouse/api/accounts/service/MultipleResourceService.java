package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

import javax.servlet.http.HttpServletRequest;

public interface MultipleResourceService<T extends RestObject> extends ResourceService<T> {

    ResponseObject<T> findAll(String commonIdentifier, HttpServletRequest request) throws DataException;

    ResponseObject<T> deleteAll(String commonIdentifier, HttpServletRequest request) throws DataException;
}
