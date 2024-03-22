package uk.gov.companieshouse.api.accounts.links;

public enum PreviousPeriodLinkType implements LinkType {

    SELF("self"),
    PROFIT_AND_LOSS("profit_and_loss");

    private final String link;

    PreviousPeriodLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
