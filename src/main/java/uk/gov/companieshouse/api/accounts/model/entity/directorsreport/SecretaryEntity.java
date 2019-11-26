package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "directors_report")
public class SecretaryEntity extends BaseEntity {

    @Field("data")
    private SecretaryDataEntity data;

    public SecretaryDataEntity getData() {
        return data;
    }

    public void setData(SecretaryDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SecretaryEntity{" +
                "data=" + data +
                '}';
    }
}
