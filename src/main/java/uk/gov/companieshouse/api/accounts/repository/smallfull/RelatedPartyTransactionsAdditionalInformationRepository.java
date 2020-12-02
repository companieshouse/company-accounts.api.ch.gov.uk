package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.AdditionalInformationEntity;

@Repository
public interface RelatedPartyTransactionsAdditionalInformationRepository extends MongoRepository<AdditionalInformationEntity, String> {
}
