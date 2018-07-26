package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface AbstractService<C extends RestObject, E extends BaseEntity> {

    C save(C rest);

    void addEtag(C rest);

    void addLinks(C rest);

    void addKind(C rest);

    void addID(E entity);

}