package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalEntity;

@Repository
public interface DirectorsApprovalRepository extends MongoRepository<DirectorsApprovalEntity, String> {
}
