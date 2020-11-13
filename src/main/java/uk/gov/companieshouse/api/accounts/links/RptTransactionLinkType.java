package uk.gov.companieshouse.api.accounts.links;

public enum RptTransactionLinkType implements LinkType {

    SELF("self");

    private String link;

    RptTransactionLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}