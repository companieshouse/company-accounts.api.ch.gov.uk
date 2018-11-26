package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
public class DebtorsEntity extends BaseEntity {

    private DebtorsDataEntity data;

    public DebtorsDataEntity getData() {
        return data;
    }

    public void setData(DebtorsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DebtorsEntity{" +
                "data=" + data +
                '}';
    }
}
