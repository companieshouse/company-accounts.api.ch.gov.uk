package uk.gov.companieshouse.api.accounts.validation;

/**
 * Validation error types
 */
public enum ErrorType {

    SERVICE("ch:service"),
    VALIDATION("ch:uk.gov.companieshouse.api.accounts.validation");

    private String type;

    ErrorType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
