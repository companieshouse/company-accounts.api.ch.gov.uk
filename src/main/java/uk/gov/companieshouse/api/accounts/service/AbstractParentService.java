package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface AbstractParentService<T extends RestObject, U extends RestObject> extends AbstractService<T> {

    void addLink(RestObject U, String id, LinkType linkType, String link, String requestId)
            throws DataException;

}