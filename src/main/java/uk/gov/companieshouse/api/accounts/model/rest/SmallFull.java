package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SmallFull extends RestObject {

    @JsonProperty("next_accounts")
    private NextAccounts nextAccounts;

    @JsonProperty("last_accounts")
    private LastAccounts lastAccounts;

    public NextAccounts getNextAccounts() {
        return nextAccounts;
    }

    public void setNextAccounts(NextAccounts nextAccounts) {
        this.nextAccounts = nextAccounts;
    }

    public LastAccounts getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(LastAccounts lastAccounts) {
        this.lastAccounts = lastAccounts;
    }

}
