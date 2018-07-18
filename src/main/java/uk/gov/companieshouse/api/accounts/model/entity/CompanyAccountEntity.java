package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "accounts")
public class CompanyAccountEntity implements BaseEntity{

    @Id
    @Field("_id")
    private String id;

    @Field("data")
    private CompanyAccountDataEntity data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CompanyAccountDataEntity getData() {
        return data;
    }

    public void setData(CompanyAccountDataEntity data) {
        this.data = data;
    }

}