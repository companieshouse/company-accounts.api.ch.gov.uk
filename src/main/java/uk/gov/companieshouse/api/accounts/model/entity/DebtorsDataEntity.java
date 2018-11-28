package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class DebtorsDataEntity extends BaseDataEntity {

    @Field("current_period_debtors")
    private CurrentPeriodDebtorsEntity currentPeriodDebtorsEntity;

    @Field("previous_period_debtors")
    private PreviousPeriodDebtorsEntity previousPeriodDebtorsEntity;

    public CurrentPeriodDebtorsEntity getCurrentPeriodDebtorsEntity() {
        return currentPeriodDebtorsEntity;
    }

    public void setCurrentPeriodDebtorsEntity(CurrentPeriodDebtorsEntity currentPeriodDebtorsEntity) {
        this.currentPeriodDebtorsEntity = currentPeriodDebtorsEntity;
    }

    public PreviousPeriodDebtorsEntity getPreviousPeriodDebtorsEntity() {
        return previousPeriodDebtorsEntity;
    }

    public void setPreviousPeriodDebtorsEntity(PreviousPeriodDebtorsEntity previousPeriodDebtorsEntity) {
        this.previousPeriodDebtorsEntity = previousPeriodDebtorsEntity;
    }

    @Override
    public String toString() {
        return "DebtorsDataEntity{" +
                "currentPeriodDebtorsEntity=" + currentPeriodDebtorsEntity +
                ", previousPeriodDebtorsEntity=" + previousPeriodDebtorsEntity +
                '}';
    }
}
