package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error message keys
 */
public enum ErrorMessageKeys {

    INCORRECT_TOTAL("incorrect_total"),
    VALUE_OUTSIDE_RANGE("value_outside_range");

    private String key;

    ErrorMessageKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
