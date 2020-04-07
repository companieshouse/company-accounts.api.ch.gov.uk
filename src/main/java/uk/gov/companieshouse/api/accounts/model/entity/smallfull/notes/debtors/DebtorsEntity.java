package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class DebtorsEntity extends NoteEntity {

    private DebtorsDataEntity data;

    public DebtorsDataEntity getData() {
        return data;
    }

    public void setData(DebtorsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DebtorsEntity{" +
                "data=" + data +
                '}';
    }
}
