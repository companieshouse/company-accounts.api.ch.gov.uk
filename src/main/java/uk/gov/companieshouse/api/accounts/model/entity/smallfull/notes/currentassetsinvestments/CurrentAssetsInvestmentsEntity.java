package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.currentassetsinvestments;

import com.google.gson.Gson;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

import java.util.Objects;

public class CurrentAssetsInvestmentsEntity extends NoteEntity {

    private CurrentAssetsInvestmentsDataEntity data;

    public CurrentAssetsInvestmentsDataEntity getData() {
        return data;
    }

    public void setData(CurrentAssetsInvestmentsDataEntity data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof CurrentAssetsInvestmentsEntity)) {return false;}
        CurrentAssetsInvestmentsEntity that = (CurrentAssetsInvestmentsEntity) o;
        return Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
