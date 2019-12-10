package uk.gov.companieshouse.api.accounts.transformer;

import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public interface GenericTransformerForMultipleResources<C extends RestObject, E extends BaseEntity>
        extends GenericTransformer<C, E> {

    C[] transform(E[] entity);
}
