package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.tangibleassets;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class TangibleAssetsEntity extends NoteEntity {

    private TangibleAssetsDataEntity data;

    public TangibleAssetsDataEntity getData() {
        return data;
    }

    public void setData(TangibleAssetsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TangibleAssetsEntity{" +
                "data=" + data +
                '}';
    }
}
