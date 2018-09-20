package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface ParentService<T extends RestObject> extends ResourceService<T> {

    void addLink(String id, LinkType linkType, String link, String requestId)
        throws DataException;

}