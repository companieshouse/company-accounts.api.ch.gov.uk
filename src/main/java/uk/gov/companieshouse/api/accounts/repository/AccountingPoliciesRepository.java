package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;

@Repository
public interface AccountingPoliciesRepository extends MongoRepository<AccountingPoliciesEntity, String> {

}
