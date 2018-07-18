package uk.gov.companieshouse.api.accounts.transformer;

import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface GenericTransformer<E extends BaseEntity, C extends RestObject> {

    E transform(C entity);

    C transform(E entity);
}
