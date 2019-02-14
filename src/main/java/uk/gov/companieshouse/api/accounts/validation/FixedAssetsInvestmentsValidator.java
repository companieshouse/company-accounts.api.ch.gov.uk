package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class FixedAssetsInvestmentsValidator extends BaseValidator {

    @Autowired
    public FixedAssetsInvestmentsValidator() {
    }

    public Errors validateFixedAssetsInvestments(@Valid FixedAssetsInvestments fixedAssetsInvestments, Transaction transaction,
            String companyAccountsId,
            HttpServletRequest request) throws DataException {

        return new Errors();
    }
}
