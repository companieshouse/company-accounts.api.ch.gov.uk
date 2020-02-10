package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

@Repository
public interface NoteRepository extends MongoRepository<NoteEntity, String> {

}
