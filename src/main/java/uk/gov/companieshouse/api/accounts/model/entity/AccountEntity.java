package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "accounts")
public class AccountEntity {

    @Id
    @Field("_id")
    private String id;

    @Field("data")
    private AccountDataEntity data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AccountDataEntity getData() {
        return data;
    }

    public void setData(AccountDataEntity data) {
        this.data = data;
    }

}