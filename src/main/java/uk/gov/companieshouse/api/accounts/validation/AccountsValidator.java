package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccountsValidator {

    public Errors validationSubmission(Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        return new Errors();
    }
}
