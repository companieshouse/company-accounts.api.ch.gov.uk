package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class CreditorsAfterMoreThanOneYearValidator extends BaseValidator implements NoteValidator<CreditorsAfterMoreThanOneYear> {

    private static final String CREDITORS_AFTER_PATH = "$.creditors_after_more_than_one_year";
    private static final String CREDITORS_AFTER_CURRENT_PERIOD_PATH = CREDITORS_AFTER_PATH +
            ".current_period";
    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_PATH = CREDITORS_AFTER_PATH +
            ".previous_period";
    private static final String CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_AFTER_PREVIOUS_PERIOD_PATH + ".total";

    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public CreditorsAfterMoreThanOneYearValidator(CompanyService companyService,
                                                  CurrentPeriodService currentPeriodService,
                                                  PreviousPeriodService previousPeriodService) {
        super(companyService);
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    private Errors validateIfEmptyResource(CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear,
                                           HttpServletRequest request, String companyAccountsId) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if ((currentPeriodBalanceSheet == null && previousPeriodBalanceSheet == null) &&
                (creditorsAfterMoreThanOneYear.getCurrentPeriod() == null &&
                        creditorsAfterMoreThanOneYear.getPreviousPeriod() == null)) {

            addEmptyResourceError(errors, CREDITORS_AFTER_PATH);
        }

        return errors;
    }

    @Override
    public Errors validateSubmission(CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear,
            Transaction transaction, String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = validateIfEmptyResource(creditorsAfterMoreThanOneYear, request, companyAccountsId);

        if (errors.hasErrors()) {
            return errors;
        }

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        CurrentPeriod currentPeriodNote = creditorsAfterMoreThanOneYear.getCurrentPeriod();
        PreviousPeriod previousPeriodNote = creditorsAfterMoreThanOneYear.getPreviousPeriod();

        validateCurrentPeriod(currentPeriodNote, currentPeriodBalanceSheet, errors);

        if (isMultipleYearFiler) {
            validatePreviousPeriod(previousPeriodNote, previousPeriodBalanceSheet, errors);
        } else {
            validatePreviousPeriodNotPresent(creditorsAfterMoreThanOneYear.getPreviousPeriod(), errors);
        }

        return errors;
    }

    private void validateCurrentPeriod(CurrentPeriod currentPeriodNote, BalanceSheet currentPeriodBalanceSheet,
                                       Errors errors) {

        if(currentPeriodBalanceSheet != null && currentPeriodNote != null) {

            validateCurrentPeriodFields(currentPeriodNote, errors);
            crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);

        } else if (currentPeriodBalanceSheet == null && currentPeriodNote != null) {

            validateCurrentPeriodFields(currentPeriodNote, errors);

        } else if (currentPeriodBalanceSheet != null){

            crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);
        }

    }

    private void validatePreviousPeriod(PreviousPeriod previousPeriodNote, BalanceSheet previousPeriodBalanceSheet,
                                        Errors errors) {

        if(previousPeriodBalanceSheet != null && previousPeriodNote != null) {

            validatePreviousPeriodFields(previousPeriodNote, errors);
            crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet, errors);

        } else if (previousPeriodBalanceSheet == null && previousPeriodNote != null) {

            validatePreviousPeriodFields(previousPeriodNote, errors);

        } else if (previousPeriodBalanceSheet != null){

            crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet, errors);
        }
    }

    private void validatePreviousPeriodNotPresent(PreviousPeriod previousPeriodCreditors,
            Errors errors) {

        if (previousPeriodCreditors != null) {
            addError(errors, unexpectedData, CREDITORS_AFTER_PREVIOUS_PERIOD_PATH);
        }
    }

    private void validatePreviousPeriodFields(PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {

        if (creditorsPreviousPeriod.getTotal() != null) {
            validatePreviousPeriodTotalCalculation(creditorsPreviousPeriod, errors);
        }
    }

    private void validatePreviousPeriodTotalCalculation(PreviousPeriod creditorsPreviousPeriod, Errors errors) {

        Long bankLoans =
                Optional.ofNullable(creditorsPreviousPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long amountsDueUnderFinance =
                Optional.ofNullable(creditorsPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long otherCreditors =
                Optional.ofNullable(creditorsPreviousPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsPreviousPeriod.getTotal();
        Long sum =
                bankLoans + amountsDueUnderFinance + otherCreditors;

        validateAggregateTotal(total, sum,
                CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH, errors);
    }

    private void validateCurrentPeriodFields(CurrentPeriod creditorsCurrentPeriod, Errors errors) {

        if (creditorsCurrentPeriod.getTotal() != null) {
            validateCurrentPeriodTotalCalculation(creditorsCurrentPeriod, errors);
        }
    }

    private void validateCurrentPeriodTotalCalculation(CurrentPeriod creditorsCurrentPeriod, Errors errors) {

        Long bankLoans =
                Optional.ofNullable(creditorsCurrentPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long amountsDueUnderFinance =
                Optional.ofNullable(creditorsCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long otherCreditors =
                Optional.ofNullable(creditorsCurrentPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsCurrentPeriod.getTotal();
        Long sum =
                bankLoans + amountsDueUnderFinance + otherCreditors;

        validateAggregateTotal(total, sum, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH
                , errors);
    }

    private void crossValidatePreviousPeriodFields(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        checkIfPreviousNoteIsNullAndBalanceNot(previousPeriodNote, previousPeriodBalanceSheet,
                errors);
        checkIsPreviousBalanceNullAndNoteNot(previousPeriodNote, previousPeriodBalanceSheet,
                errors);
        checkIfPreviousBalanceAndNoteValuesAreEqual(previousPeriodNote,
                previousPeriodBalanceSheet, errors);
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
                (! isPreviousPeriodNoteDataNull(previousPeriodNote))
                && (! previousPeriodNote.getTotal().equals(
                previousPeriodBalanceSheet.getOtherLiabilitiesOrAssets()
                        .getCreditorsAfterOneYear()))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
                (! isPreviousPeriodNoteDataNull(previousPeriodNote))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (isPreviousPeriodNoteDataNull(previousPeriodNote) &&
                (! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
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

    private void crossValidateCurrentPeriodFields(CurrentPeriod currentPeriodCreditors,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        checkIfCurrentNoteIsNullAndBalanceSheetNot(currentPeriodCreditors,
                currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(currentPeriodCreditors,
                currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceAndNoteValuesAreEqual(currentPeriodCreditors,
                currentPeriodBalanceSheet, errors);
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        if (! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)
                && ! isCurrentPeriodNoteDataNull(currentPeriodNote)
                && ! (currentPeriodNote.getTotal().equals(currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets()
                .getCreditorsAfterOneYear()))) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        if (isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet) &&
                ! isCurrentPeriodNoteDataNull(currentPeriodNote)) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        if (isCurrentPeriodNoteDataNull(currentPeriodNote) &&
                ! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
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

    private boolean isCurrentPeriodNoteDataNull(CurrentPeriod currentPeriodCreditors) {

        return ! Optional.ofNullable(currentPeriodCreditors)
                .map(CurrentPeriod :: getTotal)
                .isPresent();

    }

    private boolean isCurrentPeriodBalanceSheetDataNull(
            BalanceSheet currentPeriodBalanceSheet) {

        return ! Optional.ofNullable(currentPeriodBalanceSheet)
                .map(BalanceSheet :: getOtherLiabilitiesOrAssets)
                .map(OtherLiabilitiesOrAssets :: getCreditorsAfterOneYear)
                .isPresent();

    }

    private boolean isPreviousPeriodBalanceSheetDataNull(
            BalanceSheet previousPeriodBalanceSheet) {

        return ! Optional.ofNullable(previousPeriodBalanceSheet)
                .map(BalanceSheet :: getOtherLiabilitiesOrAssets)
                .map(OtherLiabilitiesOrAssets :: getCreditorsAfterOneYear)
                .isPresent();
    }

    private boolean isPreviousPeriodNoteDataNull(PreviousPeriod previousPeriodNote) {

        return ! Optional.ofNullable(previousPeriodNote)
                .map(PreviousPeriod :: getTotal)
                .isPresent();
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_CREDITORS_AFTER;
    }
}

