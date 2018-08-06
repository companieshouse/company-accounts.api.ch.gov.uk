package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface AbstractService<C extends RestObject, E extends BaseEntity> {
  
    C save(C rest, String companyAccountId) throws NoSuchAlgorithmException;

    void addEtag(C rest);

    void addLinks(C rest);

    void addKind(C rest);

    String getResourceName();

    String generateID(String value) throws NoSuchAlgorithmException;
}