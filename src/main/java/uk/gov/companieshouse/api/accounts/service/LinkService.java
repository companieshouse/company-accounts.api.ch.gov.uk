package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;

public interface LinkService<U extends LinkType> {

    void addLink(String id, U linkType, String link, HttpServletRequest request)
        throws DataException;

    void removeLink(String id, U linkType, HttpServletRequest request) throws DataException;

}
