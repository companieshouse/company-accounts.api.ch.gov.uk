package uk.gov.companieshouse.api.accounts.transaction;

import org.springframework.http.ResponseEntity;

public interface TransactionManager {

    ResponseEntity<Transaction> getTransaction(String id, String requestId);

    void updateTransaction(String transactionId, String requestId, String link) throws Exception ;
}