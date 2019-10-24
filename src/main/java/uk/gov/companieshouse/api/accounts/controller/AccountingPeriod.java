package uk.gov.companieshouse.api.accounts.controller;

import java.util.Arrays;

public enum AccountingPeriod {
    CURRENT_PERIOD("current-period"), PREVIOUS_PERIOD("previous-period");

    private String accountingPeriod;

    AccountingPeriod(String s) {
        this.accountingPeriod = s;
    }

    private String getPeriod() {
        return accountingPeriod;
    }

    public static AccountingPeriod fromString(String period) {
        return Arrays.stream(values())
                .filter(periodEnum -> periodEnum.getPeriod().equals(period))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown period: " + period));
    }
}
