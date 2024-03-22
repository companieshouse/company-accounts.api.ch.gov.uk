package uk.gov.companieshouse.api.accounts.links;

public enum TransactionLinkType implements LinkType {

    SELF("self"),
    PAYMENTS("payments"),
    RESOURCE("resource"),
    VALIDATION_STATUS("validation_status"),
    COSTS("costs");

    private final String link;

    TransactionLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}
