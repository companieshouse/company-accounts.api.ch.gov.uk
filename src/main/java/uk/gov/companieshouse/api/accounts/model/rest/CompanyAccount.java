package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class CompanyAccount extends RestObject {

    @JsonProperty("next_accounts")
    private AccountingPeriod nextAccounts;

    @JsonProperty("last_accounts")
    private AccountingPeriod lastAccounts;

    public AccountingPeriod getNextAccounts() {
        return nextAccounts;
    }

    public void setNextAccounts(AccountingPeriod nextAccounts) {
        this.nextAccounts = nextAccounts;
    }

    public AccountingPeriod getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(AccountingPeriod lastAccounts) {
        this.lastAccounts = lastAccounts;
    }
}
