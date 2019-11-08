package uk.gov.companieshouse.api.accounts.enumeration;

import java.util.Arrays;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;

public enum AccountsResource {

    SMALL_FULL_STOCKS(AccountType.SMALL_FULL, Resource.STOCKS, Parent.SMALL_FULL, SmallFullLinkType.STOCKS_NOTE, "small-full-accounts#stocks-note");

    private AccountType accountType;
    private Resource resource;
    private Parent parent;
    private LinkType linkType;
    private Period period;
    private String kind;

    AccountsResource(AccountType accountType, Resource resource, Parent parent, LinkType linkType, String kind) {
        this.accountType = accountType;
        this.resource = resource;
        this.parent = parent;
        this.linkType = linkType;
        this.kind = kind;
    }

    AccountsResource(AccountType accountType, Resource resource, Parent parent, LinkType linkType, Period period, String kind) {
        this.accountType = accountType;
        this.resource = resource;
        this.parent = parent;
        this.linkType = linkType;
        this.period = period;
        this.kind = kind;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Resource getResource() {
        return resource;
    }

    public Parent getParent() { return parent; }

    public LinkType getLinkType() { return linkType; }

    public Period getPeriod() {
        return period;
    }

    public String getKind() {
        return kind;
    }

    public static AccountsResource getAccountsResource(AccountType accountType, Resource resourceName, Period period) {

        return Arrays.stream(values())
                .filter(resource ->
                        resource.getAccountType().equals(accountType) &&
                                resource.getResource().equals(resourceName) &&
                                        ((period != null) ? resource.getPeriod().equals(period) : resource.getPeriod() == null))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
