package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class AccountingPeriod {

    @JsonProperty("period_start_on")
    private LocalDate periodStartOn;

    @JsonProperty("period_end_on")
    private LocalDate periodEndOn;

    public LocalDate getPeriodStartOn() { return periodStartOn; }

    public void setPeriodStartOn(LocalDate periodStartOn) { this.periodStartOn = periodStartOn; }

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }
}
