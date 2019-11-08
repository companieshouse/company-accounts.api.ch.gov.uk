package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.MINIMAL_CLASS, include = As.EXISTING_PROPERTY, property = "accountsResourcePackage")
public class Rest extends RestObject {

}
