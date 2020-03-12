package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements;

import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class OffBalanceSheetArrangementsEntity extends NoteEntity {

    private OffBalanceSheetArrangementsDataEntity data;

    public OffBalanceSheetArrangementsDataEntity getData() {
        return data;
    }

    public void setData(OffBalanceSheetArrangementsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OffBalanceSheetArrangementsEntity{" +
                "data=" + data +
                '}';
    }
}
