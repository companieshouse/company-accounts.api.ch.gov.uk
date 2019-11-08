package uk.gov.companieshouse.api.accounts.enumeration;

import java.util.Arrays;

public enum AccountType {

    SMALL_FULL("small-full");

    AccountType(String type) {
        this.type = type;
    }

    public static AccountType fromString(String type) {
        return Arrays.stream(values())
                .filter(accountType -> accountType.getType().equals(type))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Unknown account type: " + type));
    }

    private String type;

    public String getType() {
        return type;
    }
}
