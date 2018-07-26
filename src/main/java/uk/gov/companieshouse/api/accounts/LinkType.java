package uk.gov.companieshouse.api.accounts;

public enum LinkType {
    SELF("self"), RESOURCE("resource");
    private String link;

    LinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

}