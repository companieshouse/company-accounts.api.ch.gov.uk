package uk.gov.companieshouse.api.accounts.enumeration;

import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;

public enum AccountType {

    SMALL_FULL("small-full", CompanyAccountLinkType.SMALL_FULL),
    MICRO("micro", CompanyAccountLinkType.MICRO);

    AccountType(String type, CompanyAccountLinkType companyAccountLinkType) {

        this.type = type;
        this.companyAccountLinkType = companyAccountLinkType;
    }

    private String type;

    private CompanyAccountLinkType companyAccountLinkType;

    public String getType() {
        return type;
    }

    public CompanyAccountLinkType getCompanyAccountLinkType() {
        return companyAccountLinkType;
    }
}
