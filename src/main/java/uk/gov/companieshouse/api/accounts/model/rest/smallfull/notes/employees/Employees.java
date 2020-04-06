package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

import javax.validation.Valid;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Employees extends Note {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof Employees)) {return false;}
        Employees that = (Employees) o;
        return Objects.equals(getCurrentPeriod(), that.getCurrentPeriod()) &&
            Objects.equals(getPreviousPeriod(), that.getPreviousPeriod());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCurrentPeriod(), getPreviousPeriod());
    }

    @Override
    public String toString() {
        return "Employees [currentPeriod=" + currentPeriod + ", previousPeriod=" + previousPeriod
                + "]";
    }
}
