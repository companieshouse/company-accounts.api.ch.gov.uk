package uk.gov.companieshouse.api.accounts.transaction;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * Interface to the Transaction API .
 *
 * @author dparameswaran
 *
 */
public interface TransactionManager {

    ResponseEntity<Transaction> getTransaction(String id, String requestId);
}