package uk.gov.companieshouse.api.accounts.service;

import jakarta.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;

public interface RelatedPartyTransactionsService {

    public void addRptTransaction(String companyAccountsId, String rptTransactionId, String link,
            HttpServletRequest request) throws DataException;

    public void removeRptTransaction(String companyAccountsId, String rptTransactionId,
            HttpServletRequest request) throws DataException;

    public void removeAllRptTransactions(String companyAccountsId)
            throws DataException;
}
