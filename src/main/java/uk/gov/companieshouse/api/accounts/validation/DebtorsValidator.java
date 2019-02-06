package uk.gov.companieshouse.api.accounts.validation;

import com.mongodb.MongoException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class DebtorsValidator extends BaseValidator implements CrossValidator<Debtors> {

    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";
    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";

    private CompanyService companyService;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public DebtorsValidator(CompanyService companyService,
            CurrentPeriodService currentPeriodService,
            PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public Errors validateDebtors(@Valid Debtors debtors, Transaction transaction,
            String companyAccountsId,
            HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        if (debtors != null) {

            if (debtors.getCurrentPeriod() != null) {

                validateCurrentPeriodDebtors(errors, debtors);
                crossValidateCurrentPeriod(errors, request, debtors, companyAccountsId);
            }

            if (debtors.getPreviousPeriod() != null) {

                try {
                    if (companyService.isMultipleYearFiler(transaction)) {

                        validatePreviousPeriodDebtors(errors, debtors);
                        crossValidatePreviousPeriod(errors, request, companyAccountsId, debtors);
                    } else {

                        addInconsistentDataError(errors, DEBTORS_PATH_PREVIOUS);
                    }
                } catch (ServiceException e) {
                    throw new DataException(e.getMessage(), e);
                }
            }
        }
        return errors;
    }

    private void validatePreviousPeriodDebtors(Errors errors, Debtors debtors) {

        validateRequiredPreviousPeriodTotalFieldNotNull(debtors, errors);
        validatePreviousPeriodTotalIsCorrect(debtors, errors);
    }


    private void validateCurrentPeriodDebtors(Errors errors, Debtors debtors) {
        validateRequiredCurrentPeriodTotalFieldNotNull(debtors, errors);
        validateCurrentPeriodTotalIsCorrect(debtors, errors);
    }

    private void validateRequiredCurrentPeriodTotalFieldNotNull(Debtors debtors, Errors errors) {

        if ((debtors.getCurrentPeriod().getTradeDebtors() != null ||
                debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome() != null ||
                debtors.getCurrentPeriod().getOtherDebtors() != null ||
                debtors.getCurrentPeriod().getGreaterThanOneYear() != null ||
                debtors.getCurrentPeriod().getDetails() != null) &&
                debtors.getCurrentPeriod().getTotal() == null) {

            addError(errors, invalidNote, CURRENT_TOTAL_PATH);
        }
    }

    private void validateRequiredPreviousPeriodTotalFieldNotNull(Debtors debtors, Errors errors) {

        if ((debtors.getPreviousPeriod().getTradeDebtors() != null ||
                debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome() != null ||
                debtors.getPreviousPeriod().getOtherDebtors() != null ||
                debtors.getPreviousPeriod().getGreaterThanOneYear() != null) &&
                debtors.getPreviousPeriod().getTotal() == null) {

            addError(errors, invalidNote, PREVIOUS_TOTAL_PATH);
        }
    }

    private void validateCurrentPeriodTotalIsCorrect(Debtors debtors, Errors errors) {

        if (debtors.getCurrentPeriod().getTotal() != null) {
            Long traderDebtors =
                    Optional.ofNullable(debtors.getCurrentPeriod().getTradeDebtors()).orElse(0L);
            Long prepayments =
                    Optional.ofNullable(debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome())
                            .orElse(0L);
            Long otherDebtors =
                    Optional.ofNullable(debtors.getCurrentPeriod().getOtherDebtors()).orElse(0L);

            Long total = debtors.getCurrentPeriod().getTotal();
            Long sum = traderDebtors + prepayments + otherDebtors;

            validateAggregateTotal(total, sum, CURRENT_TOTAL_PATH, errors);
        }
    }

    private void validatePreviousPeriodTotalIsCorrect(Debtors debtors, Errors errors) {

        if (debtors.getPreviousPeriod().getTotal() != null) {
            Long traderDebtors =
                    Optional.ofNullable(debtors.getPreviousPeriod().getTradeDebtors()).orElse(0L);
            Long prepayments =
                    Optional.ofNullable(debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome())
                            .orElse(0L);
            Long otherDebtors =
                    Optional.ofNullable(debtors.getPreviousPeriod().getOtherDebtors()).orElse(0L);

            Long total = debtors.getPreviousPeriod().getTotal();
            Long sum = traderDebtors + prepayments + otherDebtors;

            validateAggregateTotal(total, sum, PREVIOUS_TOTAL_PATH, errors);
        }
    }

    /**
     * @inheritDoc
     */

    @Override
    public Errors crossValidate(Errors errors, HttpServletRequest request,
            String companyAccountsId,
            Debtors debtors) throws DataException {

        crossValidateCurrentPeriod(errors, request, debtors, companyAccountsId);
        crossValidatePreviousPeriod(errors, request, companyAccountsId, debtors);

        return errors;
    }

    private void crossValidatePreviousPeriod(Errors errors, HttpServletRequest request,
            String companyAccountsId,
            Debtors debtors) throws DataException {

        ResponseObject<PreviousPeriod> previousPeriodResponseObject =
                getPreviousPeriodResponseObject(request, companyAccountsId);

        checkIfPreviousNoteIsNullAndBalanceNot(errors, debtors, previousPeriodResponseObject);
        checkIsPrevousBalanceNullAndNoteNot(errors, debtors, previousPeriodResponseObject);
        checkIfPreviousBalanceAndNoteValuesAreEqual(errors, debtors, previousPeriodResponseObject);
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(Errors errors, Debtors debtors,
            ResponseObject<PreviousPeriod> previousPeriodResponseObject) {
        if (! isPreviousPeriodBalanceSheetDebtorsNull(previousPeriodResponseObject) &&
                (! isDebtorsNotePreviousTotalNull(debtors))

                && (! debtors.getPreviousPeriod().getTotal().equals(
                previousPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets()
                        .getDebtors()))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private void checkIsPrevousBalanceNullAndNoteNot(Errors errors, Debtors debtors,
            ResponseObject<PreviousPeriod> previousPeriodResponseObject) {
        if (isPreviousPeriodBalanceSheetDebtorsNull(previousPeriodResponseObject) &&
                (! isDebtorsNotePreviousTotalNull(debtors))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(Errors errors, Debtors debtors,
            ResponseObject<PreviousPeriod> previousPeriodResponseObject) {
        if (isDebtorsNotePreviousTotalNull(debtors) &&
                (! isPreviousPeriodBalanceSheetDebtorsNull(previousPeriodResponseObject))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
        }
    }

    private ResponseObject<PreviousPeriod> getPreviousPeriodResponseObject(
            HttpServletRequest request, String companyAccountsId) throws DataException {
        String previousPeriodId = previousPeriodService.generateID(companyAccountsId);

        ResponseObject<PreviousPeriod> previousPeriodResponseObject;

        try {
            previousPeriodResponseObject =
                    previousPeriodService.findById(previousPeriodId, request);
        } catch (MongoException e) {

            throw new DataException(e.getMessage(), e);
        }
        return previousPeriodResponseObject;
    }

    private void crossValidateCurrentPeriod(Errors errors, HttpServletRequest request,
            Debtors debtors,
            String companyAccountsId) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject =
                getCurrentPeriodResponseObject(request, companyAccountsId);

        checkIfCurrentNoteIsNullAndBalanceSheetNot(errors, debtors, currentPeriodResponseObject);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(errors, debtors, currentPeriodResponseObject);
        checkIfCurrentBalanceAndNoteValuesAreEqual(errors, debtors, currentPeriodResponseObject);
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual(Errors errors, Debtors debtors,
            ResponseObject<CurrentPeriod> currentPeriodResponseObject) {

        if (! (isCurrentPeriodBalanceSheetDebtorsNull(currentPeriodResponseObject)) &&
                (! isDebtorsNoteCurrentTotalNull(debtors))

                && ! (debtors.getCurrentPeriod().getTotal().equals(currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets()
                .getDebtors()))) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(Errors errors, Debtors debtors,
            ResponseObject<CurrentPeriod> currentPeriodResponseObject) {
        if (isCurrentPeriodBalanceSheetDebtorsNull(currentPeriodResponseObject) &&
                (! isDebtorsNoteCurrentTotalNull(debtors))) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(Errors errors, Debtors debtors,
            ResponseObject<CurrentPeriod> currentPeriodResponseObject) {
        if (isDebtorsNoteCurrentTotalNull(debtors) &&
                currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets() != null &&
                currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets()
                        .getDebtors() != null) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);
        }
    }

    private ResponseObject<CurrentPeriod> getCurrentPeriodResponseObject(HttpServletRequest request,
            String companyAccountsId) throws DataException {
        String currentPeriodId = currentPeriodService.generateID(companyAccountsId);

        ResponseObject<CurrentPeriod> currentPeriodResponseObject;

        try {

            currentPeriodResponseObject = currentPeriodService.findById(currentPeriodId, request);
        } catch (MongoException e) {

            throw new DataException(e.getMessage(), e);
        }
        return currentPeriodResponseObject;
    }

    private boolean isDebtorsNoteCurrentTotalNull(Debtors debtors) {
        return debtors.getCurrentPeriod() == null || debtors.getCurrentPeriod().getTotal() == null;
    }

    private boolean isCurrentPeriodBalanceSheetDebtorsNull(
            ResponseObject<CurrentPeriod> currentPeriodResponseObject) {

        return currentPeriodResponseObject.getData() == null ||
                currentPeriodResponseObject.getData().getBalanceSheet() == null ||
                currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets() == null ||
                currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets()
                        .getDebtors() == null;
    }

    private boolean isPreviousPeriodBalanceSheetDebtorsNull(
            ResponseObject<PreviousPeriod> previousPeriodResponseObject) {

        return previousPeriodResponseObject.getData() == null ||
                previousPeriodResponseObject.getData().getBalanceSheet() == null ||
                previousPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets() == null ||
                previousPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets()
                        .getDebtors() == null;
    }

    private boolean isDebtorsNotePreviousTotalNull(Debtors debtors) {

        return debtors.getPreviousPeriod() == null ||
                debtors.getPreviousPeriod().getTotal() == null;
    }
}
