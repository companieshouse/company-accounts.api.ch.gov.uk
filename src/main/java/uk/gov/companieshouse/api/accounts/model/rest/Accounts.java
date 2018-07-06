package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class Accounts {

    @NotNull
    @JsonProperty("period_end_on")
    private LocalDate periodEndOn;

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Accounts accounts = (Accounts) o;
        return Objects.equals(periodEndOn, accounts.periodEndOn);
    }

    @Override
    public int hashCode() {

        return Objects.hash(periodEndOn);
    }
}
