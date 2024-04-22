package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class CreditorsAfterMoreThanOneYearDataEntity extends BaseDataEntity {

    @Field("current_period")
    private CurrentPeriodEntity currentPeriodEntity;

    @Field("previous_period")
    private PreviousPeriodEntity previousPeriodEntity;

    public CurrentPeriodEntity getCurrentPeriodEntity() {
        return currentPeriodEntity;
    }

    public void setCurrentPeriodEntity(CurrentPeriodEntity currentPeriodEntity) {
        this.currentPeriodEntity = currentPeriodEntity;
    }

    public PreviousPeriodEntity getPreviousPeriodEntity() {
        return previousPeriodEntity;
    }

    public void setPreviousPeriodEntity(PreviousPeriodEntity previousPeriodEntity) {
        this.previousPeriodEntity = previousPeriodEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CreditorsAfterMoreThanOneYearDataEntity))
            return false;
        CreditorsAfterMoreThanOneYearDataEntity that = (CreditorsAfterMoreThanOneYearDataEntity) o;
        return Objects.equals(getCurrentPeriodEntity(), that.getCurrentPeriodEntity()) &&
                Objects.equals(getPreviousPeriodEntity(), that.getPreviousPeriodEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrentPeriodEntity(), getPreviousPeriodEntity());
    }

    @Override
    public String toString() {
        return "CreditorsAfterMoreThanOneYearDataEntity{" +
                "currentPeriodEntity=" + currentPeriodEntity +
                ", previousPeriodEntity=" + previousPeriodEntity +
                '}';
    }
}
