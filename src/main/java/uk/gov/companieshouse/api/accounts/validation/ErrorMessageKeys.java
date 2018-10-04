package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error message keys
 */
public enum ErrorMessageKeys {

    INCORRECT_TOTAL("incorrect_total"),
    MANDATORY_ELEMENT_MISSING("mandatory_element_missing"),
    VALUE_OUTSIDE_RANGE("value_outside_range"),
    INVALID_VALUE("invalid_value"),
    PAST_OR_PRESENT_DATE("must_be_a_date_in_the_past_or_in_the_present"),
    MUST_BE_A_FUTURE_DATE("must_be_a_future_date"),
    MUST_BE_A_PAST_DATE("must_be_a_past_date");

    private String key;

    ErrorMessageKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
