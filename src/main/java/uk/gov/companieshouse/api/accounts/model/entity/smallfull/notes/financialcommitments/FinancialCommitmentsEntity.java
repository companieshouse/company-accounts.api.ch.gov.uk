package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class FinancialCommitmentsEntity extends NoteEntity {

    private FinancialCommitmentsDataEntity data;

    public FinancialCommitmentsDataEntity getData() {
        return data;
    }

    public void setData(FinancialCommitmentsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FinancialCommitmentsEntity{" +
                "data=" + data +
                '}';
    }
}
