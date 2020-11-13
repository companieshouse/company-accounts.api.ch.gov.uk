package uk.gov.companieshouse.api.accounts.repository.smallfull;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionEntity;

@Repository
public interface RptTransactionRepository extends MongoRepository<RptTransactionEntity, String> {

    @Query(value = "{'data.links.transactions' : ?0}")
    RptTransactionEntity[] findAllTransactions(String transactionsLink);

    @Query(value = "{'data.links.transactions' : ?0}", delete = true)
    void deleteAllTransactions(String transactionsLink);
}
