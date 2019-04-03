package uk.gov.companieshouse.api.accounts.links;

public enum CompanyAccountLinkType implements LinkType {

    SELF("self"),
    SMALL_FULL("small_full_accounts"),
    TRANSACTION("transaction"),
    CIC34_REPORT("cic34_report");

    private String link;

    CompanyAccountLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}