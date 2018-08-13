package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface AbstractService<T extends RestObject, U extends BaseEntity> {

    T save(T rest, String companyAccountId) throws NoSuchAlgorithmException;

    U findById(String id);

    void addEtag(T rest);

    void addLinks(T rest);

    void addKind(T rest);

    String getResourceName();

    String generateID(String value) throws NoSuchAlgorithmException;
}