package uk.gov.companieshouse.api.accounts;

public enum ResourceName {

    COMPANY_ACCOUNT("company-accounts"),
    SMALL_FULL("small-full"),
    CURRENT_PERIOD("current-period");

    private String name;

    ResourceName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
