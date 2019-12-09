package uk.gov.companieshouse.api.accounts.service.response;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

public class ResponseObject<T extends RestObject> {

    private ResponseStatus status;

    private T data;

    private T[] dataForMultipleResources;

    private Errors errors;

    public ResponseObject(ResponseStatus status) {
        this.status = status;
    }

    public ResponseObject(ResponseStatus status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseObject(ResponseStatus status, T[] dataForMultipleResources) {
        this.status = status;
        this.dataForMultipleResources = dataForMultipleResources;
    }

    public ResponseObject(ResponseStatus status, Errors errors) {
        this.status = status;
        this.errors = errors;
    }

    public ResponseObject(ResponseStatus status, T data, Errors errors) {
        this.status = status;
        this.data = data;
        this.errors = errors;
    }


    public T[] getDataForMultipleResources() {
        return dataForMultipleResources;
    }

    public void setDataForMultipleResources(T[] dataForMultipleResources) {
        this.dataForMultipleResources = dataForMultipleResources;
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