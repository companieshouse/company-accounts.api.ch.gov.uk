package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class CurrentPeriodDataEntity extends BaseDataEntity {

    @Field("balance_sheet")
    private BalanceSheetEntity balanceSheetEntity;

    public BalanceSheetEntity getBalanceSheetEntity() {
        return balanceSheetEntity;
    }

    public void setBalanceSheetEntity(
            BalanceSheetEntity balanceSheetEntity) { this.balanceSheetEntity = balanceSheetEntity; }
}
