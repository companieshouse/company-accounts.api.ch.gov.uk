package uk.gov.companieshouse.api.accounts.service.response;

public enum ResponseStatus {

    DUPLICATE_KEY_ERROR,
    MONGO_ERROR,
    TRANSACTION_PATCH_ERROR,
    ID_GENERATION_ERROR,
    VALIDATION_ERROR,
    SUCCESS_CREATED
}
