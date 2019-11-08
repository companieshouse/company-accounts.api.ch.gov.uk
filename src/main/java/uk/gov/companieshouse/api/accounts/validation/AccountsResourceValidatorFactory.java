package uk.gov.companieshouse.api.accounts.validation;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;

@Component
public class AccountsResourceValidatorFactory<R extends Rest> {

    private final EnumMap<AccountsResource, AccountsResourceValidator<R>> validatorMap = new EnumMap<>(AccountsResource.class);

    @Autowired
    public AccountsResourceValidatorFactory(List<AccountsResourceValidator<R>> validatorList) {

        for (AccountsResourceValidator<R> validator : validatorList) {

            validatorMap.put(validator.getAccountsResource(), validator);
        }
    }

    public AccountsResourceValidator<R> getValidator(AccountsResource accountsResource) {

        return validatorMap.get(accountsResource);
    }
}
