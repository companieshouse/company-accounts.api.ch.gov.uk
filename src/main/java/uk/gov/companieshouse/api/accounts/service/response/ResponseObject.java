package uk.gov.companieshouse.api.accounts.service.response;

public class ResponseObject {

    private ResponseStatus status;

    private Object data;

    private ErrorData errorData;

    public ResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public ResponseObject(ResponseStatus status, Object data) {
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

    public void setData(Object data) {
        this.data = data;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }
}
