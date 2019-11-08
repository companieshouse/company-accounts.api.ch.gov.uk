package uk.gov.companieshouse.api.accounts.enumeration;

import java.util.Arrays;

public enum Period {

    CURRENT_PERIOD("current-period"),
    PREVIOUS_PERIOD("previous-period");

    Period(String accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public static Period fromString(String type) {

        return Arrays.stream(values())
                .filter(accountingPeriod -> accountingPeriod.getAccountingPeriod().equals(type))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown period: " + type));
    }

    private String accountingPeriod;

    public String getAccountingPeriod() {
        return accountingPeriod;
    }
}
