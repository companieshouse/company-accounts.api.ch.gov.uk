package uk.gov.companieshouse.api.accounts.transaction;

public class ApiErrorResponseException extends Exception {

    public ApiErrorResponseException(String message) {
        super(message);
    }

    public ApiErrorResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}