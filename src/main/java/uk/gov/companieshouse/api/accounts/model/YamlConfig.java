package uk.gov.companieshouse.api.accounts.model;

import java.util.Map;

public class YamlConfig {

    private Map<String, AccountTypeConfig> accountTypeConfig;

    public Map<String, AccountTypeConfig> getAccountTypeConfig() {
        return accountTypeConfig;
    }

    public void setAccountTypeConfig(
            Map<String, AccountTypeConfig> accountTypeConfig) {
        this.accountTypeConfig = accountTypeConfig;
    }
}
