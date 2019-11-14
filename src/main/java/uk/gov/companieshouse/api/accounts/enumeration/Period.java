package uk.gov.companieshouse.api.accounts.enumeration;

public enum Period {

    CURRENT_PERIOD("current-period"),
    PREVIOUS_PERIOD("previous-period");

    Period(String accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    private String accountingPeriod;

    public String getAccountingPeriod() {
        return accountingPeriod;
    }
}
