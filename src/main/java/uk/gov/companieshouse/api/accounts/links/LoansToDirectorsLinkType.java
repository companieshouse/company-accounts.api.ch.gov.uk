package uk.gov.companieshouse.api.accounts.links;

public enum LoansToDirectorsLinkType implements LinkType {

    SELF("self"),
    ADDITIONAL_INFO("additional_information");

    private String link;

    LoansToDirectorsLinkType(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }
}