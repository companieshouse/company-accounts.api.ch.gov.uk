package uk.gov.companieshouse.api.accounts.model.ixbrl.period;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Period {

    @JsonProperty("current_period_start_on")
    private String currentPeriodStartOn;
    @JsonProperty("current_period_ends_on")
    private String currentPeriodEndsOn;
    @JsonProperty("previous_period_start_n")
    private String previousPeriodStartOn;
    @JsonProperty("previous_period_ends_on")
    private String previousPeriodEndsOn;

    public String getCurrentPeriodStartOn() {
        return currentPeriodStartOn;
    }

    public void setCurrentPeriodStartOn(String currentPeriodStartOn) {
        this.currentPeriodStartOn = currentPeriodStartOn;
    }

    public String getCurrentPeriodEndsOn() {
        return currentPeriodEndsOn;
    }

    public void setCurrentPeriodEndsOn(String currentPeriodEndsOn) {
        this.currentPeriodEndsOn = currentPeriodEndsOn;
    }

    public String getPreviousPeriodStartOn() {
        return previousPeriodStartOn;
    }

    public void setPreviousPeriodStartOn(String previousPeriodStartOn) {
        this.previousPeriodStartOn = previousPeriodStartOn;
    }

    public String getPreviousPeriodEndsOn() {
        return previousPeriodEndsOn;
    }

    public void setPreviousPeriodEndsOn(String previousPeriodEndsOn) {
        this.previousPeriodEndsOn = previousPeriodEndsOn;
    }
}
