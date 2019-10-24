package uk.gov.companieshouse.api.accounts.controller;

import java.util.Arrays;

public enum Period {
    CURRENT_PERIOD("current-period"), PREVIOUS_PERIOD("previous-period");

    private String period;

    Period(String s) {
        this.period = s;
    }

    private String getPeriod() {
        return period;
    }

    public static Period fromString(String period) {
        return Arrays.stream(values())
                .filter(periodEnum -> periodEnum.getPeriod().equals(period))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown period: " + period));
    }
}
