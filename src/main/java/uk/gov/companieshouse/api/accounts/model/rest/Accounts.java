package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Accounts {

    @JsonProperty("last_accounts")
    private Object lastAccounts;

    public Object getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(Object lastAccounts) {
        this.lastAccounts = lastAccounts;
    }
}
