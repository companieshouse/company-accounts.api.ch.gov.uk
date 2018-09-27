package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;

@Repository
public interface ApprovalRepository extends MongoRepository<Approval, String> {

}
