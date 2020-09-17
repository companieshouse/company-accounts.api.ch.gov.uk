package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.debtors.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class DebtorsValidator extends BaseValidator implements NoteValidator<Debtors> {

    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";
    private static final String DEBTORS_PATH_CURRENT = DEBTORS_PATH + ".current_period";
    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH_CURRENT + ".total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";

    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public DebtorsValidator(CompanyService companyService,
            CurrentPeriodService currentPeriodService,
            PreviousPeriodService previousPeriodService) {
        super(companyService);
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    private Errors validateIfEmptyResource(Debtors debtors,
            HttpServletRequest request, String companyAccountsId) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if ((currentPeriodBalanceSheet == null && previousPeriodBalanceSheet == null) &&
                (debtors.getCurrentPeriod() == null &&
                        debtors.getPreviousPeriod() == null)) {

            addEmptyResourceError(errors, DEBTORS_PATH);
        }

        return errors;
    }

    @Override
    public Errors validateSubmission(Debtors debtors, Transaction transaction, String companyAccountsId,
            HttpServletRequest request) throws DataException {

        Errors errors = validateIfEmptyResource(debtors, request, companyAccountsId);

        if (errors.hasErrors()) {
            return errors;
        }

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        CurrentPeriod currentPeriodNote = debtors.getCurrentPeriod();
        PreviousPeriod previousPeriodNote = debtors.getPreviousPeriod();

        validateCurrentPeriod(currentPeriodNote, currentPeriodBalanceSheet, errors);

        if (isMultipleYearFiler) {
            validatePreviousPeriod(previousPeriodNote, previousPeriodBalanceSheet, errors);
        } else {
            validatePreviousPeriodNotPresent(debtors.getPreviousPeriod(), errors);
        }

        return errors;
    }

    private void validateCurrentPeriod(CurrentPeriod currentPeriodNote,
                                       BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        if(currentPeriodBalanceSheet != null && currentPeriodNote != null) {
            validateCurrentPeriodFields(currentPeriodNote, errors);
            crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);
        } else if (currentPeriodBalanceSheet == null && currentPeriodNote != null) {
            validateCurrentPeriodFields(currentPeriodNote, errors);
        } else if (currentPeriodBalanceSheet != null){
            crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);
        }
    }

    private void validatePreviousPeriod(PreviousPeriod previousPeriodNote,
                                        BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        if(previousPeriodBalanceSheet != null && previousPeriodNote != null) {
            validatePreviousPeriodFields(previousPeriodNote, errors);
            crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet, errors);
        } else if (previousPeriodBalanceSheet == null && previousPeriodNote != null) {
            validatePreviousPeriodFields(previousPeriodNote, errors);
        } else if (previousPeriodBalanceSheet != null){
            crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet, errors);
        }
    }

    private void validatePreviousPeriodNotPresent(PreviousPeriod previousPeriodDebtors,
            Errors errors) {

        if (previousPeriodDebtors != null) {
            addError(errors, unexpectedData, DEBTORS_PATH_PREVIOUS);
        }
    }

    private void validateCurrentPeriodFields(CurrentPeriod debtorsCurrentPeriod, Errors errors) {

        if (debtorsCurrentPeriod.getTotal() != null) {
            validateCurrentPeriodTotalCalculation(debtorsCurrentPeriod, errors);
        }
    }

    private void validatePreviousPeriodFields(@Valid PreviousPeriod debtorsPreviousPeriod, Errors errors) {

        if (debtorsPreviousPeriod.getTotal() != null) {
            validatePreviousPeriodTotalCalculation(debtorsPreviousPeriod, errors);
        }
    }

    private void validateCurrentPeriodTotalCalculation(@Valid CurrentPeriod debtorsCurrentPeriod,
            Errors errors) {

        Long tradeDebtors =
                Optional.ofNullable(debtorsCurrentPeriod.getTradeDebtors()).orElse(0L);
        Long prepaymentsAndAccruedIncome =
                Optional.ofNullable(debtorsCurrentPeriod.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long otherDebtors =
                Optional.ofNullable(debtorsCurrentPeriod.getOtherDebtors()).orElse(0L);

        Long total = debtorsCurrentPeriod.getTotal();
        Long sum =
                tradeDebtors + prepaymentsAndAccruedIncome + otherDebtors;

        validateAggregateTotal(total, sum, CURRENT_TOTAL_PATH, errors);
    }

    private void validatePreviousPeriodTotalCalculation(@Valid PreviousPeriod debtorsPreviousPeriod, Errors errors) {

        Long tradeDebtors =
                Optional.ofNullable(debtorsPreviousPeriod.getTradeDebtors()).orElse(0L);
        Long prepaymentsAndAccruedIncome =
                Optional.ofNullable(debtorsPreviousPeriod.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long otherDebtors =
                Optional.ofNullable(debtorsPreviousPeriod.getOtherDebtors()).orElse(0L);

        Long total = debtorsPreviousPeriod.getTotal();
        Long sum =
                tradeDebtors + prepaymentsAndAccruedIncome + otherDebtors;

        validateAggregateTotal(total, sum, PREVIOUS_TOTAL_PATH, errors);
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
            String companyAccountsId) throws DataException {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject
                = currentPeriodService.find(companyAccountsId, request);

        if (currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
            return currentPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(
            HttpServletRequest request, String companyAccountsId) throws DataException {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject
                = previousPeriodService.find(companyAccountsId, request);

        if (previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
            return previousPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private void crossValidateCurrentPeriodFields(CurrentPeriod currentPeriodDebtors,
            BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        checkIfCurrentNoteIsNullAndBalanceSheetNot(currentPeriodDebtors,
                currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(currentPeriodDebtors,
                currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceAndNoteValuesAreEqual(currentPeriodDebtors,
                currentPeriodBalanceSheet, errors);
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        if (isCurrentPeriodNoteDataNull(currentPeriodNote) &&
                ! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        if (isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet) &&
                ! isCurrentPeriodNoteDataNull(currentPeriodNote)) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        if (! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)
                && ! isCurrentPeriodNoteDataNull(currentPeriodNote)
                && ! (currentPeriodNote.getTotal().equals(currentPeriodBalanceSheet.getCurrentAssets()
                .getDebtors()))) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private void crossValidatePreviousPeriodFields(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        checkIfPreviousNoteIsNullAndBalanceNot(previousPeriodNote, previousPeriodBalanceSheet,
                errors);
        checkIsPreviousBalanceNullAndNoteNot(previousPeriodNote, previousPeriodBalanceSheet,
                errors);
        checkIfPreviousBalanceAndNoteValuesAreEqual(previousPeriodNote,
                previousPeriodBalanceSheet, errors);
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        if (isPreviousPeriodNoteDataNull(previousPeriodNote) &&
                (! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        if (isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
                (! isPreviousPeriodNoteDataNull(previousPeriodNote))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        if (! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
                (! isPreviousPeriodNoteDataNull(previousPeriodNote))
                && (! previousPeriodNote.getTotal().equals(
                previousPeriodBalanceSheet.getCurrentAssets()
                        .getDebtors()))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private boolean isCurrentPeriodNoteDataNull(CurrentPeriod currentPeriodDebtors) {

        return ! Optional.ofNullable(currentPeriodDebtors)
                .map(CurrentPeriod :: getTotal)
                .isPresent();
    }

    private boolean isPreviousPeriodNoteDataNull(PreviousPeriod previousPeriodNote) {

        return ! Optional.ofNullable(previousPeriodNote)
                .map(PreviousPeriod :: getTotal)
                .isPresent();
    }

    private boolean isCurrentPeriodBalanceSheetDataNull(
            BalanceSheet currentPeriodBalanceSheet) {

        return ! Optional.ofNullable(currentPeriodBalanceSheet)
                .map(BalanceSheet :: getCurrentAssets)
                .map(CurrentAssets :: getDebtors)
                .isPresent();
    }

    private boolean isPreviousPeriodBalanceSheetDataNull(
            BalanceSheet previousPeriodBalanceSheet) {

        return ! Optional.ofNullable(previousPeriodBalanceSheet)
                .map(BalanceSheet :: getCurrentAssets)
                .map(CurrentAssets :: getDebtors)
                .isPresent();
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {

        return AccountingNoteType.SMALL_FULL_DEBTORS;
    }
}
