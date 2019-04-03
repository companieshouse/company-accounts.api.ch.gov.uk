package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportEntity;

@Repository
public interface CIC34ReportRepository extends MongoRepository<CIC34ReportEntity, String> {

}
