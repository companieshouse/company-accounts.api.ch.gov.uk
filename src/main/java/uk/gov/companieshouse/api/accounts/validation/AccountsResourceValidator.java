package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface AccountsResourceValidator<R extends Rest> {

    Errors validateSubmission(R rest, Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException;

    AccountsResource getAccountsResource();
}
