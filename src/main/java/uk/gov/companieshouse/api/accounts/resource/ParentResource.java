package uk.gov.companieshouse.api.accounts.resource;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.links.LinkType;

public interface ParentResource {

    boolean hasLink(HttpServletRequest request, LinkType linkType);
}
