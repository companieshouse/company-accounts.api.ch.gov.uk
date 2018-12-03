package uk.gov.companieshouse.api.accounts.model.entity.notes.debtors;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class DebtorsDataEntity extends BaseDataEntity {

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
    public String toString() {
        return "DebtorsDataEntity{" +
                "currentPeriodEntity=" + currentPeriodEntity +
                ", previousPeriodEntity=" + previousPeriodEntity +
                '}';
    }
}
