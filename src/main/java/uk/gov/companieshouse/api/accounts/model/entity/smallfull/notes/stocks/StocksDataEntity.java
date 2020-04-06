package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class StocksDataEntity extends BaseDataEntity {

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
}
