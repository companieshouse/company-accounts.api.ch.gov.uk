package uk.gov.companieshouse.api.accounts;

public enum ResourceName {

    COMPANY_ACCOUNT("company-accounts"),
    SMALL_FULL("small-full"),
    CURRENT_PERIOD("current-period"),
    PREVIOUS_PERIOD("previous-period"),
    APPROVAL("approval"),
    ACCOUNTING_POLICIES("accounting-policy"),
    DEBTORS("debtors"),
    STATEMENTS("statements"),
    CREDITORS_WITHIN_ONE_YEAR("creditors-within-one-year");

    private String name;

    ResourceName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
