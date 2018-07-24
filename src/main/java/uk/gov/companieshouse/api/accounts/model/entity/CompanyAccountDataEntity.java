package uk.gov.companieshouse.api.accounts.model.entity;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class CompanyAccountDataEntity extends BaseDataEntity {

    @NotNull
    @Field("period_end_on")
    private LocalDate periodEndOn;

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }

}