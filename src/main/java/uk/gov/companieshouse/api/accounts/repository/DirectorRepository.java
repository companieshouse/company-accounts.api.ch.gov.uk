package uk.gov.companieshouse.api.accounts.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;

@Repository
public interface DirectorRepository extends MongoRepository<DirectorEntity, String> {

    @Query(value = "{'data.links.directors' : ?0}")
    DirectorEntity[] findAllDirectors(String directorsLink);

    @Query(value = "{'data.links.directors' : ?0}", delete = true)
    void deleteAllDirectors(String directorsLink);
}
