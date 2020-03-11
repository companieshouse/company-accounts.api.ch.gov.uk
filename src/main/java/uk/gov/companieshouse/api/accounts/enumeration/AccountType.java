package uk.gov.companieshouse.api.accounts.enumeration;

public enum AccountType {

    SMALL_FULL("small-full"),
    MICRO("micro");

    AccountType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }
}
