package uk.gov.companieshouse.api.accounts.service.response;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public class ResponseObject<T extends RestObject> {

    private ResponseStatus status;

    private T data;

    private ErrorData errorData;

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

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }
}
