package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.intangibleassets;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class IntangibleAssetsEntity extends NoteEntity {

    private IntangibleAssetsDataEntity data;

    public IntangibleAssetsDataEntity getData() {
        return data;
    }

    public void setData(IntangibleAssetsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "IntangibleAssetEntity{" +
                "data=" + data +
                '}';
    }
}
