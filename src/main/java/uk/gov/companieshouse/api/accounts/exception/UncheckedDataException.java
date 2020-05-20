package uk.gov.companieshouse.api.accounts.exception;

public class UncheckedDataException extends RuntimeException {

    public UncheckedDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
