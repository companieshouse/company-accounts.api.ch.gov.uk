package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "statements")
public class StatementEntity extends BaseEntity {

    private StatementDataEntity data;

    public StatementDataEntity getData() {
        return data;
    }

    public void setData(StatementDataEntity data) {
        this.data = data;
    }
}
