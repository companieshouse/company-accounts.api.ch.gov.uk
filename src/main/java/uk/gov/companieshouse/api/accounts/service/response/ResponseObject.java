package uk.gov.companieshouse.api.accounts.service.response;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

public class ResponseObject<T extends RestObject> {

    private ResponseStatus status;

    private T data;

    private Errors errors;

    public ResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public ResponseObject(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}
