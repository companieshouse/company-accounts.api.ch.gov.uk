package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsEntity;

public interface DebtorsRepository  extends MongoRepository<DebtorsEntity, String> {
}
