package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "accounts")
public class CompanyAccountEntity extends BaseEntity{

    @Field("data")
    private CompanyAccountDataEntity data;

    public CompanyAccountDataEntity getData() {
        return data;
    }

    public void setData(CompanyAccountDataEntity data) {
        this.data = data;
    }

}