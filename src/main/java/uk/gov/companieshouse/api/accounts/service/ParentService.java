package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface ParentService<T extends RestObject, U extends LinkType> extends
    ResourceService<T> {

    void addLink(String id, U linkType, String link, String requestId)
        throws DataException;

}