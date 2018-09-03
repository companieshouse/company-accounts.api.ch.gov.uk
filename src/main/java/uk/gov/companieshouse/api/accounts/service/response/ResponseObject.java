package uk.gov.companieshouse.api.accounts.service.response;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public class ResponseObject<T extends RestObject> {

    private ResponseStatus status;

    private T data;

    private ValidationErrorData validationErrorData;

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

    public ValidationErrorData getValidationErrorData() {
        return validationErrorData;
    }

    public void setValidationErrorData(ValidationErrorData validationErrorData) {
        this.validationErrorData = validationErrorData;
    }
}
