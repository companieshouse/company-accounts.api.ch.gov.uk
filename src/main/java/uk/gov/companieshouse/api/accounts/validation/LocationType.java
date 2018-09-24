package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error location types
 */
public enum LocationType {

    JSON_BODY("json-body"),
    JSON_PATH("json-path");

    private String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}