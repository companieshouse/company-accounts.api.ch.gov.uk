package uk.gov.companieshouse.api.accounts.links;

public enum CurrentPeriodLinkType implements LinkType {

    SELF("self"),
    PROFIT_AND_LOSS("profit_and_loss");

    private String link;

    CurrentPeriodLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
