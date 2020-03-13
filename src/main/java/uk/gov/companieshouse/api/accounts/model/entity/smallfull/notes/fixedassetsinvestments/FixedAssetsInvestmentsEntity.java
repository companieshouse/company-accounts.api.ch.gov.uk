package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.fixedassetsinvestments;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class FixedAssetsInvestmentsEntity extends NoteEntity {

    private FixedAssetsInvestmentsDataEntity data;

    public FixedAssetsInvestmentsDataEntity getData() {
        return data;
    }

    public void setData(FixedAssetsInvestmentsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FixedAssetsInvestmentsEntity [data=" + data + "]";
    }
}
