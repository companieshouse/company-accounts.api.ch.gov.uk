package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Debtors extends RestObject {

    @JsonProperty("current_period_debtors")
    private CurrentPeriodDebtors currentPeriodDebtors;

    @JsonProperty("previous_period_debtors")
    private PreviousPeriodDebtors previousPeriodDebtors;

    public CurrentPeriodDebtors getCurrentPeriodDebtors() {
        return currentPeriodDebtors;
    }

    public void setCurrentPeriodDebtors(CurrentPeriodDebtors currentPeriodDebtors) {
        this.currentPeriodDebtors = currentPeriodDebtors;
    }

    public PreviousPeriodDebtors getPreviousPeriodDebtors() {
        return previousPeriodDebtors;
    }

    public void setPreviousPeriodDebtors(PreviousPeriodDebtors previousPeriodDebtors) {
        this.previousPeriodDebtors = previousPeriodDebtors;
    }
}
