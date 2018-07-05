package uk.gov.companieshouse.api.accounts.model.entity;

import static org.springframework.util.Assert.notNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Represents a collection of accounts
 */
@Document(collection = "accounts")
public class Accounts {

    @Id
    @Field("_id")
    @JsonProperty("id")
    private String id;

    private AccountsData data;

    /**
     * Constructor
     */
    private Accounts() {}

    /**
     * Constructor
     *
     * @param id
     * @param accountsData
     */
    public Accounts(String id, AccountsData accountsData) {
        notNull(id, "Id cannot be null");
        notNull(accountsData, "AccountsData cannot be null");

        this.id = id;
        this.data = accountsData;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public void setId(String id) {
        this.id = id;
    }

    public AccountsData getData() {
        return data;
    }

    public void setData(AccountsData data) {
        this.data = data;
    }

    @JsonMerge()
    public Map<String, String> getLinks() {
        return this.data.getLinks();
    }

    public void setLinks(Map<String, String> links) {
        this.data.setLinks(links);
    }

}