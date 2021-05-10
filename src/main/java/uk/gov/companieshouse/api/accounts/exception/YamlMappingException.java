package uk.gov.companieshouse.api.accounts.exception;

public class YamlMappingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public YamlMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
