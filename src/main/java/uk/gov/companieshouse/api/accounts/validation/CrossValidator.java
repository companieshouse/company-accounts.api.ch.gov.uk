package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

public interface CrossValidator<T> {

    /**
     * @inheritDoc
     *
     * Cross validate values from note current period values with corresponding balance sheet value
     */
    Errors crossValidateCurrentPeriod(Errors errors, HttpServletRequest request,
                                      String CompanyAccountsId,
                                      T t) throws DataException;

    /**
     * @inheritDoc
     *
     * Cross validate values from note previous period values with corresponding balance sheet value
     */

    Errors crossValidatePreviousPeriod(Errors errors, HttpServletRequest request,
                                       String CompanyAccountsId, T t) throws DataException;
}
