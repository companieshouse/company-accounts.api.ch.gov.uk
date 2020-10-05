package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.PreviousPeriodTnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class AccountsValidator {

    @Autowired
    private CompanyAccountService companyAccountService;

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private CurrentPeriodTnClosureValidator currentPeriodTnClosureValidator;

    @Autowired
    private PreviousPeriodTnClosureValidator previousPeriodTnClosureValidator;
    
    public Errors validationSubmission(Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Errors errors = new Errors();

        ResponseObject<CompanyAccount> companyAccountResponseObject = companyAccountService.findById(companyAccountsId, request);

        CompanyAccount companyAccount = companyAccountResponseObject.getData();
        
        if(companyAccount.getLinks().get(CompanyAccountLinkType.SMALL_FULL.getLink()) != null) {
            SmallFull smallFull = smallFullService.find(companyAccountsId, request).getData();
            
            // Current period validation.
            currentPeriodTnClosureValidator.isValid(companyAccountsId, smallFull, request, errors);

            // Previous period validation.
            previousPeriodTnClosureValidator.isValid(companyAccountsId, smallFull, transaction, request, errors);
        }

        return errors;
    }
}
