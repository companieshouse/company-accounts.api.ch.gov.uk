package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;

/**
 * Repository interface for {@link CompanyAccountEntity} and its data {@link CompanyAccountDataEntity}
 */
@Repository
public interface CompanyAccountRepository extends MongoRepository<CompanyAccountEntity, String> {

}