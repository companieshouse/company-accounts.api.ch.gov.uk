package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "accounts_resource_package")
public class Note extends RestObject {

    protected static final int MAX_FIELD_LENGTH = 20000;
    protected static final int MIN_FIELD_LENGTH = 1;

}
