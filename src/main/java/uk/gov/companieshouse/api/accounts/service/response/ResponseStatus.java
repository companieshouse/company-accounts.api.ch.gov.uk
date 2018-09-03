package uk.gov.companieshouse.api.accounts.service.response;

public enum ResponseStatus {

    DUPLICATE_KEY_ERROR,
    //Mongo error should be deleted after the AbstractService is refactored to the new implementation
    MONGO_ERROR,
    SUCCESS_CREATED
}
