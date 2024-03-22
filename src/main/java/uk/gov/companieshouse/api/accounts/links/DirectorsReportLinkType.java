package uk.gov.companieshouse.api.accounts.links;

public enum DirectorsReportLinkType implements LinkType {

    SELF("self"),
    STATEMENTS("statements"),
    SECRETARY("secretary"),
    APPROVAL("approval");

    private final String link;

    DirectorsReportLinkType(String link) {
        this.link = link;
    }

    @Override
    public String getLink() {
        return link;
    }
}
