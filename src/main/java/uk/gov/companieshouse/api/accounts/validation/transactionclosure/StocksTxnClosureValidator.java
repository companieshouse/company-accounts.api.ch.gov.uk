package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Component
public class StocksTxnClosureValidator extends BaseValidator {

    private final NoteService stocksNoteService;

    private final NoteValidatorFactory<Note> validatorFactory;


    private static final String SMALL_FULL_CURRENT_STOCKS = "$.company_accounts.small_full.current_period.notes.stocks";
    private static final String SMALL_FULL_PREVIOUS_STOCKS = "$.company_accounts.small_full.previous_period.notes.stocks";

    @Autowired
    public StocksTxnClosureValidator(CompanyService companyService,
                                     NoteService noteService,
                                     NoteValidatorFactory<Note> noteValidatorFactory) {
        super(companyService);
        this.stocksNoteService = noteService;
        this.validatorFactory = noteValidatorFactory;
    }

    public Errors validate(String companyAccountsId,
                           SmallFull smallFull,
                           Transaction transaction,
                           HttpServletRequest request,
                           Errors errors,
                           BalanceSheet currentPeriodBalanceSheet,
                           BalanceSheet previousPeriodBalanceSheet) throws DataException {

        ResponseObject<Note> stocksNote = stocksNoteService.find(AccountingNoteType.SMALL_FULL_STOCKS, companyAccountsId);

        if (smallFull.getLinks().get(SmallFullLinkType.STOCKS_NOTE.getLink()) != null) {
            errors = validatorFactory.getValidator(AccountingNoteType.SMALL_FULL_STOCKS).validateSubmission(stocksNote.getData(), transaction, companyAccountsId, request);
        } else {

            if (currentPeriodBalanceSheet.getCurrentAssets().getStocks() != null) {
                addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_STOCKS);
            }

            if (getIsMultipleYearFiler(transaction)) {
                if (previousPeriodBalanceSheet.getCurrentAssets().getStocks() != null) {
                    addError(errors, mandatoryElementMissing, SMALL_FULL_PREVIOUS_STOCKS);
                }
            }
        }

        return errors;
    }
}
