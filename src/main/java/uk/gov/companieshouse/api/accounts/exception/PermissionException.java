package uk.gov.companieshouse.api.accounts.exception;

public class PermissionException extends RuntimeException {

    public PermissionException(String message) {
        super(message);
    }
    
    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }
}
