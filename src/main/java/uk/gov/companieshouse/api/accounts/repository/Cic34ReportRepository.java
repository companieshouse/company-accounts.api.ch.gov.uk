package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.Cic34ReportEntity;

@Repository
public interface Cic34ReportRepository extends MongoRepository<Cic34ReportEntity, String> {

}
