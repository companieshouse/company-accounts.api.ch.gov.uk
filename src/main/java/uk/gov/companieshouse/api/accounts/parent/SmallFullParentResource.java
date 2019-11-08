package uk.gov.companieshouse.api.accounts.parent;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.Parent;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;

@Component
public class SmallFullParentResource implements ParentResource<SmallFullLinkType> {

    @Autowired
    private SmallFullService smallFullService;

    @Override
    public boolean childExists(HttpServletRequest request, SmallFullLinkType linkType) {

        return StringUtils.isNotBlank(
                ((SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue()))
                        .getLinks().get(linkType.getLink()));
    }

    @Override
    public void addLink(String companyAccountsId, SmallFullLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

        smallFullService.addLink(companyAccountsId, linkType, link, request);
    }

    @Override
    public void removeLink(String companyAccountsId, SmallFullLinkType linkType,
            HttpServletRequest request) throws DataException {

        smallFullService.removeLink(companyAccountsId, linkType, request);
    }

    @Override
    public Parent getParent() {

        return Parent.SMALL_FULL;
    }
}
