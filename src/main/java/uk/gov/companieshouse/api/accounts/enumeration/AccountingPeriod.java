package uk.gov.companieshouse.api.accounts.enumeration;

import java.util.Arrays;

public enum AccountingPeriod {
    CURRENT_PERIOD("current-period"), PREVIOUS_PERIOD("previous-period");

    private String period;

    AccountingPeriod(String s) {
        this.period = s;
    }

    private String getPeriod() {
        return period;
    }

    public static AccountingPeriod fromString(String period) {
        return Arrays.stream(values())
                .filter(periodEnum -> periodEnum.getPeriod().equals(period))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown period: " + period));
    }
}
