package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;

@Repository
public interface ApprovalRepository extends MongoRepository<ApprovalEntity, String> {

}
