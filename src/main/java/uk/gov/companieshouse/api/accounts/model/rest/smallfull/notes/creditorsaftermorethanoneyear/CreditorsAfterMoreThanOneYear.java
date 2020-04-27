package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditorsAfterMoreThanOneYear extends Note {

    @Valid
    @JsonProperty("current_period")
    private CurrentPeriod currentPeriod;

    @Valid
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
