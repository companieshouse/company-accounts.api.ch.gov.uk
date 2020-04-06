package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class StocksEntity extends NoteEntity {

    private StocksDataEntity data;

    public StocksDataEntity getData() {
        return data;
    }

    public void setData(StocksDataEntity data) {
        this.data = data;
    }
}
