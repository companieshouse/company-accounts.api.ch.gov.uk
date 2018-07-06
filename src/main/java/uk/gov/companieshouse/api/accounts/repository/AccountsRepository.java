package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDBEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsDataDBEntity;

/**
 * Repository interface for {@link AccountsDBEntity} and its data {@link AccountsDataDBEntity}
 */
public interface AccountsRepository extends MongoRepository<AccountsDBEntity, String> {

}