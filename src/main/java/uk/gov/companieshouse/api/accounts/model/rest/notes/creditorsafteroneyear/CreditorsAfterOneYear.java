package uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditorsAfterOneYear extends RestObject {


    @JsonProperty("current_period")
    private uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod currentPeriod;

    @JsonProperty("previous_period")
    private PreviousPeriod previousPeriod;

    public CurrentPeriod getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(CurrentPeriod currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public PreviousPeriod getPreviousPeriod() {
        return previousPeriod;
    }

    public void setPreviousPeriod(PreviousPeriod previousPeriod) {
        this.previousPeriod = previousPeriod;
    }
}
