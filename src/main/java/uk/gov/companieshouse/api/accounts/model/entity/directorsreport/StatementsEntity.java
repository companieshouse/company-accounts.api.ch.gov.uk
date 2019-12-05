package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "directors_report")
public class StatementsEntity extends BaseEntity {

    @Field("data")
    private StatementsDataEntity data;

    public StatementsDataEntity getData() {
        return data;
    }

    public void setData(StatementsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StatementsEntity{" +
                "data=" + data +
                "}";
    }
}
