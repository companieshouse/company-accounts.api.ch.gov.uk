package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class CompanyAccountDataEntity extends BaseDataEntity {

    @Field("next_accounts")
    private AccountingPeriodEntity nextAccounts;

    @Field("last_accounts")
    private AccountingPeriodEntity lastAccounts;

    public AccountingPeriodEntity getNextAccounts() {
        return nextAccounts;
    }

    public void setNextAccounts(
            AccountingPeriodEntity nextAccounts) {
        this.nextAccounts = nextAccounts;
    }

    public AccountingPeriodEntity getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(
            AccountingPeriodEntity lastAccounts) {
        this.lastAccounts = lastAccounts;
    }
}