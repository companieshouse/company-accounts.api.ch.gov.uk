package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportEntity;

public interface CicReportRepository extends MongoRepository<CicReportEntity, String> {

}
