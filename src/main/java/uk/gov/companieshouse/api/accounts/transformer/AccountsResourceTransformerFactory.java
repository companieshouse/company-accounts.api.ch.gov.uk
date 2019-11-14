package uk.gov.companieshouse.api.accounts.transformer;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;

@Component
public class AccountsResourceTransformerFactory<R extends Rest, E extends BaseEntity> {

    private final EnumMap<AccountsResource, AccountsResourceTransformer<R, E>> transformerMap = new EnumMap<>(AccountsResource.class);

    @Autowired
    public AccountsResourceTransformerFactory(List<AccountsResourceTransformer<R, E>> transformerList) {

        for (AccountsResourceTransformer<R, E> transformer : transformerList) {

            transformerMap.put(transformer.getAccountsResource(), transformer);
        }
    }

    public AccountsResourceTransformer<R, E> getTransformer(AccountsResource accountsResource) {

        AccountsResourceTransformer<R, E> transformer = transformerMap.get(accountsResource);

        if (transformer == null) {
            throw new MissingInfrastructureException("No transformer type for accounts resource: " + accountsResource.toString());
        }
        return transformer;
    }
}
