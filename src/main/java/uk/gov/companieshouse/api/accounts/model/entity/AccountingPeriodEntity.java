package uk.gov.companieshouse.api.accounts.model.entity;

import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Field;

public class AccountingPeriodEntity {

    @Field("period_start_on")
    private LocalDate periodStartOn;

    @Field("period_end_on")
    private LocalDate periodEndOn;

    public LocalDate getPeriodStartOn() {
        return periodStartOn;
    }

    public void setPeriodStartOn(LocalDate periodStartOn) {
        this.periodStartOn = periodStartOn;
    }

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }
}
