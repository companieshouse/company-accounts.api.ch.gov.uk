package uk.gov.companieshouse.api.accounts.model.rest;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.api.accounts.validation.WithinSetDaysOfPeriodEnd;

public class NextAccounts {

    @JsonProperty("period_start_on")
    private LocalDate periodStartOn;

    @JsonProperty("period_end_on")
    @NotNull
    @PastOrPresent
    @WithinSetDaysOfPeriodEnd(numOfDays=7)
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
