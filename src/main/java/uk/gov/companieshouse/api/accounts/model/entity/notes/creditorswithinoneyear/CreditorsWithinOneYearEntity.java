package uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

import java.util.Objects;

@Document(collection = "notes")
public class CreditorsWithinOneYearEntity extends BaseEntity {

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
