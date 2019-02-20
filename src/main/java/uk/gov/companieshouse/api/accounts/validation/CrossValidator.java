package uk.gov.companieshouse.api.accounts.validation;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import javax.servlet.http.HttpServletRequest;

public interface CrossValidator<T> {

    /**
     * @inheritDoc Cross validate values from note with corresponding balance sheet value
     * @param  errors the errors object used to contain all errors whilst validating
     * @param  request
     * @param  companyAccountsId
     * @param  t the note object that needs to be validated
     *
     * @return the errors object containing all errors added whilst validating
     */
    Errors crossValidate(T t, HttpServletRequest request, String companyAccountsId, Errors errors) throws DataException;

}
