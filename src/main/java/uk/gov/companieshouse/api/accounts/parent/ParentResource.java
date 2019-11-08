package uk.gov.companieshouse.api.accounts.parent;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.enumeration.Parent;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;

public interface ParentResource<L extends LinkType> {

    boolean childExists(HttpServletRequest request, L linkType);

    void addLink(String companyAccountsId, L linkType, String link, HttpServletRequest request)
            throws DataException;

    void removeLink(String companyAccountsId, L linkType, HttpServletRequest request)
            throws DataException;

    Parent getParent();
}
