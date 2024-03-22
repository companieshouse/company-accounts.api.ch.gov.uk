package uk.gov.companieshouse.api.accounts.links;

public enum BasicLinkType implements LinkType {

    SELF("self");

    private final String link;

    BasicLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}