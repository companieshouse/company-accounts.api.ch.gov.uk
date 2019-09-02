package uk.gov.companieshouse.api.accounts.service;

import java.util.List;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface TransactionService {

    /**
     * Get the {@link PayableResource}'s associated with a transaction
     *
     * @param transaction The transaction for which to obtain a list of payable resources
     * @return a {@link List} of {@link PayableResource}'s, or an empty list if no payments are associated with the transaction
     * @throws ServiceException if there's an error in determining a transaction's payable status
     */
    List<PayableResource> getPayableResources(Transaction transaction) throws ServiceException;
}
