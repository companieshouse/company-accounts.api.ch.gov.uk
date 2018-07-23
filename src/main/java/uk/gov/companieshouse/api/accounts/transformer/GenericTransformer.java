package uk.gov.companieshouse.api.accounts.transformer;

import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface GenericTransformer<C extends RestObject, E extends BaseEntity> {

    E transform(C entity);

    C transform(E entity);
}
