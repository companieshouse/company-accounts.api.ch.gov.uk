package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class StocksTxnClosureValidator extends BaseValidator {

    private static final String SMALL_FULL_STOCKS_LOCATION = "$.company_accounts.small_full.notes.stocks";

    private final NoteService stocksNoteService;

    private final NoteValidatorFactory<Note> validatorFactory;

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

        if (smallFull.getLinks().get(SmallFullLinkType.STOCKS_NOTE.getLink()) != null) { // Do they have a note? If yes we need to validate its data.
            ResponseObject<Note> stocksResponseObj = stocksNoteService.find(AccountingNoteType.SMALL_FULL_STOCKS, companyAccountsId);

            Note stocksData = stocksResponseObj.getData();
            if(stocksData != null) {
                NoteValidator<Note> noteValidator = validatorFactory.getValidator(AccountingNoteType.SMALL_FULL_STOCKS);
                Errors noteValidationErrors = noteValidator.validateSubmission(stocksData, transaction, companyAccountsId, request);
                if (noteValidationErrors.hasErrors()) {
                    errors.getErrors().addAll(noteValidationErrors.getErrors());
                }
            }
        } else { // if there's no stock note, then there should be no stock values on the balance sheet.


            long currentStock = Optional.of(currentPeriodBalanceSheet)
                    .map(BalanceSheet::getCurrentAssets)
                    .map(CurrentAssets::getStocks)
                    .orElse(0L);

            long previousStock = 0L;

            if(getIsMultipleYearFiler(transaction)) {
                 previousStock = Optional.of(previousPeriodBalanceSheet)
                        .map(BalanceSheet::getCurrentAssets)
                        .map(CurrentAssets::getStocks)
                        .orElse(0L);
            }
            if (currentStock != 0 || previousStock != 0) {
                addError(errors, mandatoryElementMissing, SMALL_FULL_STOCKS_LOCATION);
            }
        }

        return errors;
    }
}
