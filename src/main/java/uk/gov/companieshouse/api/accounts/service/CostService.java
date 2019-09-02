package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Cost;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface CostService {

    Cost[] getCosts(Transaction transaction) throws DataException;
}
