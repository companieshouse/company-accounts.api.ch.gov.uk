package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.CurrentPeriodTxnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.PreviousPeriodTxnClosureValidator;
import uk.gov.companieshouse.api.accounts.validation.transactionclosure.StocksTxnClosureValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class AccountsValidator extends BaseValidator {

    private static final String SMALL_FULL_PATH = "$.company_accounts.small_full";

    private final CompanyAccountService companyAccountService;

    private final SmallFullService smallFullService;

    private final CurrentPeriodTxnClosureValidator currentPeriodTnClosureValidator;

    private final PreviousPeriodTxnClosureValidator previousPeriodTnClosureValidator;

    private final StocksTxnClosureValidator stocksTnClosureValidator;

    private final CurrentPeriodService currentPeriodService;

    private final PreviousPeriodService previousPeriodService;

    @Autowired
    public AccountsValidator(CompanyService companyService,
                             CompanyAccountService companyAccountService,
                             SmallFullService smallFullService,
                             CurrentPeriodTxnClosureValidator currentPeriodTnClosureValidator,
                             PreviousPeriodTxnClosureValidator previousPeriodTnClosureValidator,
                             CurrentPeriodService currentPeriodService,
                             PreviousPeriodService previousPeriodService,
                             StocksTxnClosureValidator stocksTnClosureValidator) {
        super(companyService);
        this.companyAccountService = companyAccountService;
        this.smallFullService = smallFullService;
        this.currentPeriodTnClosureValidator = currentPeriodTnClosureValidator;
        this.previousPeriodTnClosureValidator = previousPeriodTnClosureValidator;
        this.stocksTnClosureValidator = stocksTnClosureValidator;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }
    
    public Errors validate(Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Errors errors = new Errors();

        ResponseObject<CompanyAccount> companyAccountResponseObject = companyAccountService.findById(companyAccountsId, request);

        CompanyAccount companyAccount = companyAccountResponseObject.getData();
        
        if(companyAccount.getLinks().get(CompanyAccountLinkType.SMALL_FULL.getLink()) != null) {
            SmallFull smallFull = smallFullService.find(companyAccountsId, request).getData();

            // Period validation.
            errors = currentPeriodTnClosureValidator.validate(companyAccountsId, smallFull, request, errors);
            errors = previousPeriodTnClosureValidator.validate(companyAccountsId, smallFull, transaction, request, errors);

            if (errors.hasErrors()) {
                return errors;
            }

            BalanceSheet currentPeriodBalanceSheet = currentPeriodService.find(companyAccountsId, request).getData().getBalanceSheet();
            BalanceSheet previousPeriodBalanceSheet = previousPeriodService.find(companyAccountsId, request).getData().getBalanceSheet();

            // Note validation.
            errors = stocksTnClosureValidator
                    .validate(companyAccountsId, smallFull, transaction, request, errors, currentPeriodBalanceSheet, previousPeriodBalanceSheet);

        } else {
            addError(errors, mandatoryElementMissing, SMALL_FULL_PATH);
        }

        return errors;
    }
}
