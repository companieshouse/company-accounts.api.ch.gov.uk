package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "notes")
public class AccountingPoliciesEntity extends BaseEntity {

    @Field("data")
    private AccountingPoliciesDataEntity data;

    public AccountingPoliciesDataEntity getData() {
        return data;
    }

    public void setData(AccountingPoliciesDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AccountingPoliciesEntity{" +
                "data=" + data +
                '}';
    }
}
