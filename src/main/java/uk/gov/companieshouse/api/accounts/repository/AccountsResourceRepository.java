package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Component
public interface AccountsResourceRepository<E extends BaseEntity> extends MongoRepository<E, String> {

    default AccountsResource getAccountsResource() {
        return null;
    }
}
