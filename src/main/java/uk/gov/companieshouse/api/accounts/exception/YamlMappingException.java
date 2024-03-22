package uk.gov.companieshouse.api.accounts.exception;

import java.io.Serial;

public class YamlMappingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public YamlMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
