package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Component
public class CreditorsWithinOneYearValidator extends BaseValidator implements CrossValidator<CreditorsWithinOneYear> {

    private static final String CREDITORS_WITHIN_PATH = "$.creditors_within_one_year";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".current_period";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".previous_period";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".total";

    private CompanyService companyService;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public CreditorsWithinOneYearValidator(CompanyService companyService,
            CurrentPeriodService currentPeriodService,
            PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public boolean validateIfOnlyDetails(CurrentPeriod currentPeriodNote) {
        return (currentPeriodNote != null && currentPeriodNote.getDetails() != null && creditorsNumberFieldsAreNull(currentPeriodNote));
    }

    private boolean creditorsNumberFieldsAreNull(CurrentPeriod currentPeriodNote) {
        return currentPeriodNote.getTotal() == null && currentPeriodNote.getTradeCreditors() == null && currentPeriodNote.getAccrualsAndDeferredIncome() == null && currentPeriodNote.getOtherCreditors() == null && currentPeriodNote.getTaxationAndSocialSecurity()==null && currentPeriodNote.getFinanceLeasesAndHirePurchaseContracts()==null && currentPeriodNote.getBankLoansAndOverdrafts()==null;
    }

    private Errors validateIfEmptyResource(CreditorsWithinOneYear creditorsWithinOneYear,
            HttpServletRequest request, String companyAccountsId) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if ((currentPeriodBalanceSheet == null && previousPeriodBalanceSheet == null) &&
                (creditorsWithinOneYear.getCurrentPeriod() == null &&
                        creditorsWithinOneYear.getPreviousPeriod() == null)) {

            addEmptyResourceError(errors, CREDITORS_WITHIN_PATH);
        }

        return errors;
    }

    public Errors validateCreditorsWithinOneYear(@Valid CreditorsWithinOneYear creditorsWithinOneYear,
            Transaction transaction,
            String companyAccountsId,
            HttpServletRequest request) throws DataException {

        Errors errors = validateIfEmptyResource(creditorsWithinOneYear, request, companyAccountsId);

        if (errors.hasErrors()) {
            return errors;
        }

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        CurrentPeriod currentPeriodNote = creditorsWithinOneYear.getCurrentPeriod();
        PreviousPeriod previousPeriodNote = creditorsWithinOneYear.getPreviousPeriod();

        validateCurrentPeriod(currentPeriodNote, currentPeriodBalanceSheet, errors);

        if (isMultipleYearFiler) {
            validatePreviousPeriod(previousPeriodNote, previousPeriodBalanceSheet, errors);
        } else {
            validatePreviousPeriodNotPresent(creditorsWithinOneYear.getPreviousPeriod(), errors);
        }

        return errors;
    }

    private void validateCurrentPeriod(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        boolean hasCurrentPeriodBalanceSheet = currentPeriodBalanceSheet != null;
        boolean hasCurrentPeriodBalanceSheetNoteValue =
                ! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet);
        boolean hasCurrentPeriodNoteData = currentPeriodNote != null;

        if (! hasCurrentPeriodBalanceSheetNoteValue && hasCurrentPeriodNoteData) {

            if (validateNoUnexpectedDataPresent(hasCurrentPeriodBalanceSheet,
                    CREDITORS_WITHIN_CURRENT_PERIOD_PATH, errors)) {
                validateCurrentPeriodFields(currentPeriodNote, errors);
            }

        } else if (validateCurrentPeriodExists(hasCurrentPeriodBalanceSheetNoteValue,
                hasCurrentPeriodNoteData, errors) && hasCurrentPeriodNoteData) {
            validateCurrentPeriodFields(currentPeriodNote, errors);
            crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);
        }
    }

    private boolean validateNoUnexpectedDataPresent(boolean hasCurrentPeriodBalanceSheet,
            String errorPath,
            Errors errors) {

        if (hasCurrentPeriodBalanceSheet) {
            addError(errors, unexpectedData, errorPath);
            return false;
        }

        return true;
    }

    private void validatePreviousPeriod(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        boolean hasPreviousPeriodBalanceSheet = previousPeriodBalanceSheet != null;
        boolean hasPreviousPeriodBalanceSheetNoteValue =
                ! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet);
        boolean hasPreviousPeriodNoteData = previousPeriodNote != null;

        if (! hasPreviousPeriodBalanceSheetNoteValue && hasPreviousPeriodNoteData) {

            if (validateNoUnexpectedDataPresent(hasPreviousPeriodBalanceSheet,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH, errors)) {
                validatePreviousPeriodFields(previousPeriodNote, errors);
            }

        } else if (validatePreviousPeriodExists(hasPreviousPeriodBalanceSheetNoteValue,
                hasPreviousPeriodNoteData, errors) && hasPreviousPeriodNoteData) {
            validatePreviousPeriodFields(previousPeriodNote, errors);
            crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet,
                    errors);
        }
    }

    private void validatePreviousPeriodNotPresent(PreviousPeriod previousPeriodCreditors,
            Errors errors) {

        if (previousPeriodCreditors != null) {
            addError(errors, unexpectedData, CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH);
        }
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    private void validatePreviousPeriodFields(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {

        if (creditorsPreviousPeriod.getTotal() == null) {
            addError(errors, mandatoryElementMissing, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        } else {
            validatePreviousTotalCalculationCorrect(creditorsPreviousPeriod, errors);
        }
    }

    private void validatePreviousTotalCalculationCorrect(@Valid PreviousPeriod creditorsPreviousPeriod, Errors errors) {

        Long bankLoans =
                Optional.ofNullable(creditorsPreviousPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long amountsDueUnderFinance =
                Optional.ofNullable(creditorsPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long tradeCreditors =
                Optional.ofNullable(creditorsPreviousPeriod.getTradeCreditors()).orElse(0L);
        Long taxation =
                Optional.ofNullable(creditorsPreviousPeriod.getTaxationAndSocialSecurity()).orElse(0L);
        Long accruals =
                Optional.ofNullable(creditorsPreviousPeriod.getAccrualsAndDeferredIncome()).orElse(0L);
        Long otherCreditors =
                Optional.ofNullable(creditorsPreviousPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsPreviousPeriod.getTotal();
        Long sum =
                bankLoans + amountsDueUnderFinance + tradeCreditors + taxation + accruals + otherCreditors;

        validateAggregateTotal(total, sum,
                CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH, errors);
    }

    private boolean validateCurrentPeriodExists(boolean hasCurrentPeriodBalanceSheetValue,
            boolean hasCurrentPeriodNoteData,
            Errors errors) {

        if (hasCurrentPeriodBalanceSheetValue && ! hasCurrentPeriodNoteData) {
            addError(errors, mandatoryElementMissing, CREDITORS_WITHIN_CURRENT_PERIOD_PATH);
            return false;
        } else if (! hasCurrentPeriodBalanceSheetValue && hasCurrentPeriodNoteData) {
            addError(errors, unexpectedData, CREDITORS_WITHIN_CURRENT_PERIOD_PATH);
            return false;
        }

        return true;
    }

    private boolean validatePreviousPeriodExists(boolean hasPreviousPeriodBalanceSheetValue,
            boolean hasPreviousPeriodNoteData,
            Errors errors) {

        if (hasPreviousPeriodBalanceSheetValue && ! hasPreviousPeriodNoteData) {
            addError(errors, mandatoryElementMissing, CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH);
            return false;
        } else if (! hasPreviousPeriodBalanceSheetValue && hasPreviousPeriodNoteData) {
            addError(errors, unexpectedData, CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH);
            return false;
        }

        return true;
    }

    private void validateCurrentPeriodFields(CurrentPeriod creditorsCurrentPeriod, Errors errors) {
        if (creditorsCurrentPeriod.getTotal() == null) {
            addError(errors, mandatoryElementMissing, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        } else {
            validateCurrentPeriodTotalCalculation(creditorsCurrentPeriod, errors);
        }
    }

    private void validateCurrentPeriodTotalCalculation(@Valid CurrentPeriod creditorsCurrentPeriod, Errors errors) {

        Long bankLoans =
                Optional.ofNullable(creditorsCurrentPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long amountsDueUnderFinance =
                Optional.ofNullable(creditorsCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long tradeCreditors =
                Optional.ofNullable(creditorsCurrentPeriod.getTradeCreditors()).orElse(0L);
        Long taxation =
                Optional.ofNullable(creditorsCurrentPeriod.getTaxationAndSocialSecurity()).orElse(0L);
        Long accruals =
                Optional.ofNullable(creditorsCurrentPeriod.getAccrualsAndDeferredIncome()).orElse(0L);
        Long otherCreditors =
                Optional.ofNullable(creditorsCurrentPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsCurrentPeriod.getTotal();
        Long sum =
                bankLoans + amountsDueUnderFinance + tradeCreditors + taxation + accruals + otherCreditors;

        validateAggregateTotal(total, sum, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH
                , errors);
    }

    @Override
    public Errors crossValidate(CreditorsWithinOneYear creditorsWithinOneYear,
            HttpServletRequest request,
            String companyAccountsId,
            Errors errors) throws DataException {

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        crossValidateCurrentPeriodFields(creditorsWithinOneYear.getCurrentPeriod(),
                currentPeriodBalanceSheet, errors);
        crossValidatePreviousPeriodFields(creditorsWithinOneYear.getPreviousPeriod(),
                previousPeriodBalanceSheet, errors);

        return errors;
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
                        .getCreditorsDueWithinOneYear()))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
                (! isPreviousPeriodNoteDataNull(previousPeriodNote))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(PreviousPeriod previousPeriodNote,
            BalanceSheet previousPeriodBalanceSheet,
            Errors errors) {

        if (isPreviousPeriodNoteDataNull(previousPeriodNote) &&
                (! isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
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
                .getCreditorsDueWithinOneYear()))) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        if (isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet) &&
                ! isCurrentPeriodNoteDataNull(currentPeriodNote)) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(CurrentPeriod currentPeriodNote,
            BalanceSheet currentPeriodBalanceSheet,
            Errors errors) {

        if (isCurrentPeriodNoteDataNull(currentPeriodNote) &&
                ! isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)) {

            addError(errors, currentBalanceSheetNotEqual,
                    CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
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
                .map(OtherLiabilitiesOrAssets :: getCreditorsDueWithinOneYear)
                .isPresent();

    }

    private boolean isPreviousPeriodBalanceSheetDataNull(
            BalanceSheet previousPeriodBalanceSheet) {

        return ! Optional.ofNullable(previousPeriodBalanceSheet)
                .map(BalanceSheet :: getOtherLiabilitiesOrAssets)
                .map(OtherLiabilitiesOrAssets :: getCreditorsDueWithinOneYear)
                .isPresent();
    }

    private boolean isPreviousPeriodNoteDataNull(PreviousPeriod previousPeriodNote) {

        return ! Optional.ofNullable(previousPeriodNote)
                .map(PreviousPeriod :: getTotal)
                .isPresent();
    }
}
