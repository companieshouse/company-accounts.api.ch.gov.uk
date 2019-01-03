package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

public interface CrossValidator<T> {

    /**
     * @inheritDoc Cross validate values from note values with corresponding balance sheet value
     */
    Errors crossValidate(Errors errors, HttpServletRequest request, String CompanyAccountsId,
                         T t) throws DataException;

}
