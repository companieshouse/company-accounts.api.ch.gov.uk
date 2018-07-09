package uk.gov.companieshouse.api.accounts.model.entity;

import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.rest.Account;

@Document(collection = "accounts")
public class AccountEntity {

    @Id
    @Field("_id")
    private String id;

    @Field("data")
    private AccountDataEntity data;

    public AccountEntity build(Account account) {
        this.id = Base64.getUrlEncoder().encodeToString(UUID.randomUUID().toString().substring(0, 20).getBytes());
        this.data = new AccountDataEntity().build(this.id);
        BeanUtils.copyProperties(account, this.data);
        return this;
    }

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