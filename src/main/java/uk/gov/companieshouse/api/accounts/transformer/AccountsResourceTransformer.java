package uk.gov.companieshouse.api.accounts.transformer;

import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;

public interface AccountsResourceTransformer<C extends Rest, E extends BaseEntity> {

    E transform(C entity);

    C transform(E entity);

    AccountsResource getAccountsResource();
}
