package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
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
public class StocksValidator extends BaseValidator implements CrossValidator<Stocks> {

    private static final String STOCKS_PATH = "$.stocks";
    private static final String STOCKS_CURRENT_PERIOD_PATH =
        STOCKS_PATH + ".current_period";
    private static final String STOCKS_PREVIOUS_PERIOD_PATH =
        STOCKS_PATH + ".previous_period";
    private static final String STOCKS_CURRENT_PERIOD_TOTAL_PATH =
        STOCKS_CURRENT_PERIOD_PATH + ".total";
    private static final String STOCKS_PREVIOUS_PERIOD_TOTAL_PATH =
        STOCKS_PREVIOUS_PERIOD_PATH + ".total";

    private CompanyService companyService;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public StocksValidator(CompanyService companyService, CurrentPeriodService currentPeriodService,
                           PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public Errors validateStocks(@Valid Stocks stocks, Transaction transaction,
                                 String companyAccountsId,
                                 HttpServletRequest request) throws DataException {


        Errors errors = validateIfEmptyResource(stocks, request, companyAccountsId);

        if (errors.hasErrors()) {
            return errors;
        }

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request, companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request, companyAccountsId);

        CurrentPeriod currentPeriodNote = stocks.getCurrentPeriod();
        PreviousPeriod previousPeriodNote = stocks.getPreviousPeriod();

        validateCurrentPeriod(currentPeriodNote, currentPeriodBalanceSheet, errors);

        if (isMultipleYearFiler) {
            validatePreviousPeriod(previousPeriodNote, previousPeriodBalanceSheet, errors);
        } else {
            validatePreviousPeriodNotPresent(stocks.getPreviousPeriod(), errors);
        }

        return errors;
    }

    private Errors validateIfEmptyResource(Stocks stocks,
            HttpServletRequest request, String companyAccountsId) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if ((currentPeriodBalanceSheet == null && previousPeriodBalanceSheet == null) &&
                (stocks.getCurrentPeriod() == null &&
                        stocks.getPreviousPeriod() == null)) {

            addEmptyResourceError(errors, STOCKS_PATH);
        }

        return errors;
    }

    private void validateCurrentPeriod(CurrentPeriod currentPeriodNote, BalanceSheet currentPeriodBalanceSheet, Errors errors) {

        boolean hasCurrentPeriodBalanceSheet = currentPeriodBalanceSheet != null;
        boolean hasCurrentPeriodBalanceSheetNoteValue = !isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet);
        boolean hasCurrentPeriodNoteData = currentPeriodNote != null;

        if (!hasCurrentPeriodBalanceSheetNoteValue && hasCurrentPeriodNoteData) {

            if (validateNoUnexpectedDataPresent(hasCurrentPeriodBalanceSheet, STOCKS_CURRENT_PERIOD_PATH, errors)) {
                validateCurrentPeriodFields(currentPeriodNote, errors);
            }

        } else if (validateCurrentPeriodExists(hasCurrentPeriodBalanceSheetNoteValue, hasCurrentPeriodNoteData, errors)) {

            if (hasCurrentPeriodNoteData) {
                validateCurrentPeriodFields(currentPeriodNote, errors);
            }

            if (hasCurrentPeriodNoteData && hasCurrentPeriodBalanceSheetNoteValue) {
                crossValidateCurrentPeriodFields(currentPeriodNote, currentPeriodBalanceSheet, errors);
            }
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

    private void validatePreviousPeriod(PreviousPeriod previousPeriodNote, BalanceSheet previousPeriodBalanceSheet, Errors errors) {

        boolean hasPreviousPeriodBalanceSheet = previousPeriodBalanceSheet != null;
        boolean hasPreviousPeriodBalanceSheetNoteValue = !isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet);
        boolean hasPreviousPeriodNoteData = previousPeriodNote != null;

        if (!hasPreviousPeriodBalanceSheetNoteValue && hasPreviousPeriodNoteData) {

            if (validateNoUnexpectedDataPresent(hasPreviousPeriodBalanceSheet, STOCKS_PREVIOUS_PERIOD_PATH, errors)) {
                validatePreviousPeriodFields(previousPeriodNote, errors);
            }

        } else if (validatePreviousPeriodExists(hasPreviousPeriodBalanceSheetNoteValue, hasPreviousPeriodNoteData, errors)) {

            if (hasPreviousPeriodNoteData) {
                validatePreviousPeriodFields(previousPeriodNote, errors);
            }

            if (hasPreviousPeriodNoteData && hasPreviousPeriodBalanceSheetNoteValue) {
                crossValidatePreviousPeriodFields(previousPeriodNote, previousPeriodBalanceSheet, errors);
            }
        }
    }

    private void validatePreviousPeriodNotPresent(PreviousPeriod previousPeriod,
                                                  Errors errors) {

        if (previousPeriod != null) {
            addError(errors, unexpectedData, STOCKS_PREVIOUS_PERIOD_PATH);
        }
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    private void validatePreviousPeriodFields(@Valid PreviousPeriod previousPeriod,
                                              Errors errors) {

        if (previousPeriod.getTotal() == null) {
            addError(errors, mandatoryElementMissing, STOCKS_PREVIOUS_PERIOD_TOTAL_PATH);
        } else {
            validatePreviousTotalCalculationCorrect(previousPeriod, errors);
        }
    }

    private void validatePreviousTotalCalculationCorrect(@Valid PreviousPeriod previousPeriod, Errors errors) {

        Long paymentsOnAccount =
            Optional.ofNullable(previousPeriod.getPaymentsOnAccount()).orElse(0L);
        Long stocks = Optional.ofNullable(previousPeriod.getStocks()).orElse(0L);
        Long total = previousPeriod.getTotal();
        Long sum = paymentsOnAccount + stocks;

        validateAggregateTotal(total, sum, STOCKS_PREVIOUS_PERIOD_TOTAL_PATH, errors);
    }

    private boolean validateCurrentPeriodExists(boolean hasCurrentPeriodBalanceSheetValue,
                                                boolean hasCurrentPeriodNoteData,
                                                Errors errors) {

        if (hasCurrentPeriodBalanceSheetValue && !hasCurrentPeriodNoteData) {
            addError(errors, mandatoryElementMissing, STOCKS_CURRENT_PERIOD_PATH);
            return false;
        } else if (!hasCurrentPeriodBalanceSheetValue && hasCurrentPeriodNoteData) {
            addError(errors, unexpectedData, STOCKS_CURRENT_PERIOD_PATH);
            return false;
        }

        return true;
    }

    private boolean validatePreviousPeriodExists(boolean hasPreviousPeriodBalanceSheetValue,
                                                 boolean hasPreviousPeriodNoteData,
                                                 Errors errors) {

        if (hasPreviousPeriodBalanceSheetValue && !hasPreviousPeriodNoteData) {
            addError(errors, mandatoryElementMissing, STOCKS_PREVIOUS_PERIOD_PATH);
            return false;
        } else if (!hasPreviousPeriodBalanceSheetValue && hasPreviousPeriodNoteData) {
            addError(errors, unexpectedData, STOCKS_PREVIOUS_PERIOD_PATH);
            return false;
        }

        return true;
    }

    private void validateCurrentPeriodFields(CurrentPeriod currentPeriod, Errors errors) {
        if (currentPeriod.getTotal() == null) {
            addError(errors, mandatoryElementMissing, STOCKS_CURRENT_PERIOD_TOTAL_PATH);
        } else {
            validateCurrentPeriodTotalCalculation(currentPeriod, errors);
        }
    }

    private void validateCurrentPeriodTotalCalculation(@Valid CurrentPeriod currentPeriod, Errors errors) {

        Long paymentsOnAccount =
            Optional.ofNullable(currentPeriod.getPaymentsOnAccount()).orElse(0L);
        Long stocks = Optional.ofNullable(currentPeriod.getStocks()).orElse(0L);
        Long total = currentPeriod.getTotal();
        Long sum = paymentsOnAccount + stocks;

        validateAggregateTotal(total, sum, STOCKS_CURRENT_PERIOD_TOTAL_PATH, errors);
    }

    @Override
    public Errors crossValidate(Stocks stocks,
                                HttpServletRequest request,
                                String companyAccountsId,
                                Errors errors) throws DataException {

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request, companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request, companyAccountsId);

        crossValidateCurrentPeriodFields(stocks.getCurrentPeriod(), currentPeriodBalanceSheet, errors);
        crossValidatePreviousPeriodFields(stocks.getPreviousPeriod(), previousPeriodBalanceSheet, errors);

        return errors;
    }

    private void crossValidatePreviousPeriodFields(PreviousPeriod previousPeriodNote,
                                                   BalanceSheet previousPeriodBalanceSheet,
                                                   Errors errors) {

        checkIfPreviousNoteIsNullAndBalanceNot(previousPeriodNote, previousPeriodBalanceSheet, errors);
        checkIsPreviousBalanceNullAndNoteNot(previousPeriodNote, previousPeriodBalanceSheet, errors);
        checkIfPreviousBalanceAndNoteValuesAreEqual(previousPeriodNote, previousPeriodBalanceSheet, errors);
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(PreviousPeriod previousPeriodNote,
                                                             BalanceSheet previousPeriodBalanceSheet,
                                                             Errors errors) {

        if (!isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
            (!isPreviousPeriodNoteDataNull(previousPeriodNote))
            && (!previousPeriodNote.getTotal().equals(
            previousPeriodBalanceSheet.getCurrentAssets()
                .getStocks()))) {

            addError(errors, previousBalanceSheetNotEqual, STOCKS_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(PreviousPeriod previousPeriodNote,
                                                      BalanceSheet previousPeriodBalanceSheet,
                                                      Errors errors) {

        if (isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet) &&
            (!isPreviousPeriodNoteDataNull(previousPeriodNote))) {

            addError(errors, previousBalanceSheetNotEqual, STOCKS_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(PreviousPeriod previousPeriodNote,
                                                        BalanceSheet previousPeriodBalanceSheet,
                                                        Errors errors) {

        if (isPreviousPeriodNoteDataNull(previousPeriodNote) &&
            (!isPreviousPeriodBalanceSheetDataNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual, STOCKS_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(
        HttpServletRequest request, String companyAccountsId) throws DataException {
        String previousPeriodId = previousPeriodService.generateID(companyAccountsId);

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject;

        previousPeriodResponseObject = previousPeriodService.findById(previousPeriodId, request);

        if (previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
            return previousPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private void crossValidateCurrentPeriodFields(CurrentPeriod currentPeriodCreditors,
                                                  BalanceSheet currentPeriodBalanceSheet,
                                                  Errors errors) {

        checkIfCurrentNoteIsNullAndBalanceSheetNot(currentPeriodCreditors, currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(currentPeriodCreditors, currentPeriodBalanceSheet, errors);
        checkIfCurrentBalanceAndNoteValuesAreEqual(currentPeriodCreditors, currentPeriodBalanceSheet, errors);
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual(CurrentPeriod currentPeriodNote,
                                                            BalanceSheet currentPeriodBalanceSheet,
                                                            Errors errors) {

        if (!isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)
            && !isCurrentPeriodNoteDataNull(currentPeriodNote)
            && !(currentPeriodNote.getTotal().equals(currentPeriodBalanceSheet.getCurrentAssets()
            .getStocks()))) {

            addError(errors, currentBalanceSheetNotEqual, STOCKS_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(CurrentPeriod currentPeriodNote,
                                                            BalanceSheet currentPeriodBalanceSheet,
                                                            Errors errors) {

        if (isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet) &&
            !isCurrentPeriodNoteDataNull(currentPeriodNote)) {

            addError(errors, currentBalanceSheetNotEqual, STOCKS_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(CurrentPeriod currentPeriodNote,
                                                            BalanceSheet currentPeriodBalanceSheet,
                                                            Errors errors) {

        if (isCurrentPeriodNoteDataNull(currentPeriodNote) &&
            !isCurrentPeriodBalanceSheetDataNull(currentPeriodBalanceSheet)) {

            addError(errors, currentBalanceSheetNotEqual, STOCKS_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        String currentPeriodId = currentPeriodService.generateID(companyAccountsId);

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject;

        currentPeriodResponseObject = currentPeriodService.findById(currentPeriodId, request);

        if (currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
            return currentPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private boolean isCurrentPeriodNoteDataNull(CurrentPeriod currentPeriodCreditors) {

        return !Optional.ofNullable(currentPeriodCreditors)
            .map(CurrentPeriod::getTotal)
            .isPresent();

    }

    private boolean isCurrentPeriodBalanceSheetDataNull(
        BalanceSheet currentPeriodBalanceSheet) {

        return !Optional.ofNullable(currentPeriodBalanceSheet)
            .map(BalanceSheet::getCurrentAssets)
            .map(CurrentAssets::getStocks)
            .isPresent();

    }

    private boolean isPreviousPeriodBalanceSheetDataNull(
        BalanceSheet previousPeriodBalanceSheet) {

        return !Optional.ofNullable(previousPeriodBalanceSheet)
            .map(BalanceSheet::getCurrentAssets)
            .map(CurrentAssets::getStocks)
            .isPresent();
    }

    private boolean isPreviousPeriodNoteDataNull(PreviousPeriod previousPeriodNote) {

        return !Optional.ofNullable(previousPeriodNote)
            .map(PreviousPeriod::getTotal)
            .isPresent();
    }
}
