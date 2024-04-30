package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error location types
 */
public enum LocationType {

    REQUEST_BODY("request-body"),
    JSON_PATH("json-path");

    private final String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}