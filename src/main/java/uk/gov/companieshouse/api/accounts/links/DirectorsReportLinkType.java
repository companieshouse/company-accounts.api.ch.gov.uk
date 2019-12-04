package uk.gov.companieshouse.api.accounts.links;

public enum DirectorsReportLinkType implements LinkType {

    SELF("self"),
    STATEMENTS("statements"),
    SECRETARY("secretary"),
    APPROVAL("approval");

    private String link;

    DirectorsReportLinkType(String link) {
        this.link = link;
    }

    @Override
    public String getLink() {
        return link;
    }
}
