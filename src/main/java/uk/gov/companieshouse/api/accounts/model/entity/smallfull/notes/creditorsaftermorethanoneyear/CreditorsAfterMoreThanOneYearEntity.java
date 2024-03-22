package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear;

import java.util.Objects;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class CreditorsAfterMoreThanOneYearEntity extends NoteEntity {

    private CreditorsAfterMoreThanOneYearDataEntity data;

    public CreditorsAfterMoreThanOneYearDataEntity getData() {
        return data;
    }

    public void setData(CreditorsAfterMoreThanOneYearDataEntity data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CreditorsAfterMoreThanOneYearEntity))
            return false;
        CreditorsAfterMoreThanOneYearEntity that = (CreditorsAfterMoreThanOneYearEntity) o;
        return Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData());
    }

    @Override
    public String toString() {
        return "CreditorsAfterMoreThanOneYearEntity{" +
                "data=" + data +
                '}';
    }
}
