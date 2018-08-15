package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

public interface AbstractService<T extends RestObject, U extends BaseEntity> {

    ResponseObject<T> save(T rest, String companyAccountId);

    U findById(String id);

    void addEtag(T rest);

    void addKind(T rest);

    String getResourceName();

    String generateID(String value) throws NoSuchAlgorithmException;
}