package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountDataEntity;

/**
 * Repository interface for {@link AccountEntity} and its data {@link AccountDataEntity}
 */
@Repository
public interface AccountRepository extends MongoRepository<AccountEntity, String> {

}