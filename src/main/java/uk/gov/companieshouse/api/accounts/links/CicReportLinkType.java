package uk.gov.companieshouse.api.accounts.links;

public enum CicReportLinkType implements LinkType {

    SELF("self"),
    STATEMENTS("statements"),
    APPROVAL("approval");

    private final String link;

    CicReportLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
