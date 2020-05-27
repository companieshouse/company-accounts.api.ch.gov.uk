package uk.gov.companieshouse.api.accounts.parent;

import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;

public interface ParentResource<L extends LinkType> {

    boolean childExists(HttpServletRequest request, L linkType);

    void addLink(String companyAccountsId, L linkType, String link, HttpServletRequest request)
            throws DataException;

    void removeLink(String companyAccountsId, L linkType, HttpServletRequest request)
            throws DataException;

    AccountType getParent();

    LocalDate getPeriodStartOn(HttpServletRequest request);

    LocalDate getPeriodEndOn(HttpServletRequest request);

    LocalDate getPeriodEndOn(String companyAccountsId, HttpServletRequest request);
}
