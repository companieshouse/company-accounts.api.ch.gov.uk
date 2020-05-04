package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorswithinoneyear;

import com.google.gson.Gson;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

import java.util.Objects;

public class CreditorsWithinOneYearEntity extends NoteEntity {

    private CreditorsWithinOneYearDataEntity data;

    public CreditorsWithinOneYearDataEntity getData() {
        return data;
    }

    public void setData(CreditorsWithinOneYearDataEntity data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof CreditorsWithinOneYearEntity)) {return false;}
        CreditorsWithinOneYearEntity that = (CreditorsWithinOneYearEntity) o;
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
