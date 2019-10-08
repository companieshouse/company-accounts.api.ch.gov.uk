package uk.gov.companieshouse.api.accounts.resource;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@Component
public class SmallFullResource implements ParentResource {

    @Override
    public boolean hasLink(HttpServletRequest request, LinkType linkType) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        return smallFull.getLinks().get(linkType.getLink()) != null;
    }
}
