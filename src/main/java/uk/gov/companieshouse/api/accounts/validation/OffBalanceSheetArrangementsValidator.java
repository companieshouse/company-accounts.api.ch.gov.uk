package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Component
public class OffBalanceSheetArrangementsValidator extends BaseValidator implements NoteValidator<OffBalanceSheetArrangements> {

    private static final String OFF_BALANCE_SHEET_ARRANGEMENTS_PATH = "$.off_balance_sheet_arrangements";

    private static final String OFF_BALANCE_SHEET_ARRANGEMENTS_DETAILS_PATH = "$.off_balance_sheet_arrangements.details";

    @Override
    public Errors validateSubmission(OffBalanceSheetArrangements note, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validateIfEmptyResource(note, request, companyAccountId);

        return errors;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {

        return AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;
    }

    private Errors validateIfEmptyResource(OffBalanceSheetArrangements offBalanceSheetArrangements,
                                           HttpServletRequest request, String companyAccountsId) throws DataException {

        Errors errors = new Errors();

        if(offBalanceSheetArrangements == null) {

            addEmptyResourceError(errors, OFF_BALANCE_SHEET_ARRANGEMENTS_PATH);
        }

        return errors;
    }
}
