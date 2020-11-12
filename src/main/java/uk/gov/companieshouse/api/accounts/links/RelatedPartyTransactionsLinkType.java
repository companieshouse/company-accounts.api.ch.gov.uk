package uk.gov.companieshouse.api.accounts.links;

public enum RelatedPartyTransactionsLinkType implements LinkType {

    SELF("self"),
    ADDITIONAL_INFO("additional_information");

    private String link;

    RelatedPartyTransactionsLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}