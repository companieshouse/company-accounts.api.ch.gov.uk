package uk.gov.companieshouse.api.accounts.repository;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Component
public class AccountsResourceRepositoryFactory<E extends BaseEntity> {

    private final EnumMap<AccountsResource, AccountsResourceRepository<E>> repositoryMap = new EnumMap<>(AccountsResource.class);

    @Autowired
    public AccountsResourceRepositoryFactory(List<AccountsResourceRepository<E>> repositoryList) {

        for(AccountsResourceRepository<E> repository : repositoryList) {

            if (repository.getAccountsResource() != null) {

                repositoryMap.put(repository.getAccountsResource(), repository);
            }
        }
    }

    public AccountsResourceRepository<E> getRepository(AccountsResource accountsResource) {

        AccountsResourceRepository<E> repository = repositoryMap.get(accountsResource);

        if (repository == null) {
            throw new MissingInfrastructureException("No repository type for accounts resource: " + accountsResource.toString());
        }
        return repository;
    }
}
