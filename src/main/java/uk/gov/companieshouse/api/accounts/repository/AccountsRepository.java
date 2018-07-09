package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDataEntity;

/**
 * Repository interface for {@link AccountsEntity} and its data {@link AccountsDataEntity}
 */
public interface AccountsRepository extends MongoRepository<AccountsEntity, String> {

}