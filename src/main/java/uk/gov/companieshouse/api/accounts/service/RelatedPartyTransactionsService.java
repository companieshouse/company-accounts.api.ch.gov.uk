package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;

public interface RelatedPartyTransactionsService {

    void addRptTransaction(String companyAccountsId, String rptTransactionId, String link,
            HttpServletRequest request) throws DataException;

    public void removeAllRptTransactions(String companyAccountsId)
            throws DataException;
}
