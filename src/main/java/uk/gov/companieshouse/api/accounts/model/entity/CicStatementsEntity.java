package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic_report")
public class CicStatementsEntity extends BaseEntity {

    @Field("data")
    private CicStatementsDataEntity data;

    public CicStatementsDataEntity getData() {
        return data;
    }

    public void setData(CicStatementsDataEntity data) {
        this.data = data;
    }
}
