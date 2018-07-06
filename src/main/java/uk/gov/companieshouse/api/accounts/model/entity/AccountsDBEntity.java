package uk.gov.companieshouse.api.accounts.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;

@Document(collection = "accounts")
public class AccountsDBEntity {

    @Id
    @Field("_id")
    private String id;

    @Field("data")
    private AccountsDataDBEntity data;

    public AccountsDBEntity build(Accounts accounts) {
        this.id = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().substring(0, 20).getBytes());
        this.data = new AccountsDataDBEntity().build(this.id);
        BeanUtils.copyProperties(accounts, this.data);
        return this;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    public AccountsDataDBEntity getData() {
        return data;
    }

    public void setData(AccountsDataDBEntity data) {
        this.data = data;
    }

}