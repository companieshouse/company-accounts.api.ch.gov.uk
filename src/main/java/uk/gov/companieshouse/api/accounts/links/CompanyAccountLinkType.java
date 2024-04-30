package uk.gov.companieshouse.api.accounts.links;

public enum CompanyAccountLinkType implements LinkType {

    SELF("self"),
    SMALL_FULL("small_full_accounts"),
    MICRO("micro_accounts"),
    TRANSACTION("transaction"),
    CIC_REPORT("cic_report");

    private final String link;

    CompanyAccountLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}