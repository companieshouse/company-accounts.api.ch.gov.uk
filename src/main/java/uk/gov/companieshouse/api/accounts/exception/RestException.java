package uk.gov.companieshouse.api.accounts.exception;

import org.springframework.web.client.RestClientException;

public class RestException extends RestClientException {

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }
}
