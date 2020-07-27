package uk.gov.companieshouse.api.accounts.service;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;

public interface LoansToDirectorsService {

    void addLoan(String companyAccountsId, String loanId, String link, HttpServletRequest request)
            throws DataException;

    void removeLoan(String companyAccountsId, String loanId, HttpServletRequest request)
            throws DataException;
}
