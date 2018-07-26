package uk.gov.companieshouse.api.accounts;

public enum Kind {

    COMPANY_ACCOUNTS("company-accounts"),
    SMALL_FULL_ACCOUNT("accounts#smallfull");

    private String value;

    Kind(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}