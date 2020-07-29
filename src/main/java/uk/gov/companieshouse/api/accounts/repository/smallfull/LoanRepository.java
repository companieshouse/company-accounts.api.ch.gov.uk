package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;

@Repository
public interface LoanRepository extends MongoRepository<LoanEntity, String> {

    @Query(value = "{'data.links.loans' : ?0}")
    LoanEntity[] findAllLoans(String loansLink);

    @Query(value = "{'data.links.loans' : ?0}", delete = true)
    void deleteAllLoans(String loansLink);
}
