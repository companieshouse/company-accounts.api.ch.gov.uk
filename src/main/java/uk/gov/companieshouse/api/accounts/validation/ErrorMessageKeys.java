package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error message keys
 */
public enum ErrorMessageKeys {

    INCORRECT_TOTAL("incorrect_total"),
    MANDATORY_ELEMENT_MISSING("mandatory_element_missing"),
    VALUE_OUTSIDE_RANGE("value_outside_range"),
    INVALID_VALUE("invalid_value");

    private String key;

    ErrorMessageKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
