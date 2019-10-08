package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public interface Validator<R extends RestObject> {

    Errors validateSubmission(R rest, Transaction transaction, String companyAccountsId, HttpServletRequest request) throws DataException;
}
