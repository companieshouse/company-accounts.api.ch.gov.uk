package uk.gov.companieshouse.api.accounts.model.entity.notes.stocks;

import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

public class StocksEntity extends BaseEntity {

    private StocksDataEntity data;

    public StocksDataEntity getData() {
        return data;
    }

    public void setData(StocksDataEntity data) {
        this.data = data;
    }
}
