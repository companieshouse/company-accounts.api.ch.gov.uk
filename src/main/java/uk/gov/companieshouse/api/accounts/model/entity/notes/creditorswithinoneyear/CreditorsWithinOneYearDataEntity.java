package uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.util.Objects;

public class CreditorsWithinOneYearDataEntity extends BaseDataEntity {

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
        if (this == o) {return true;}
        if (!(o instanceof CreditorsWithinOneYearDataEntity)) {return false;}
        CreditorsWithinOneYearDataEntity that = (CreditorsWithinOneYearDataEntity) o;
        return Objects.equals(getCurrentPeriodEntity(), that.getCurrentPeriodEntity()) &&
            Objects.equals(getPreviousPeriodEntity(), that.getPreviousPeriodEntity());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getCurrentPeriodEntity(), getPreviousPeriodEntity());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
