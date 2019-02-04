package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Component
public class CreditorsAfterOneYearValidator extends BaseValidator implements CrossValidator<CreditorsAfterOneYear> {

    @Value("${invalid.note}")
    private String invalidNote;

    @Value("${current.balancesheet.not.equal}")
    private String currentBalanceSheetNotEqual;

    @Value("${previous.balancesheet.not.equal}")
    private String previousBalanceSheetNotEqual;

    private static final String CREDITORS_AFTER_PATH = "$.creditors_after_one_year";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_PATH =
        CREDITORS_AFTER_PATH + ".current_period";

    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_PATH =
        CREDITORS_AFTER_PATH + ".previous_period";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH =
        CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".total";

    private static final String CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH =
        CREDITORS_AFTER_PREVIOUS_PERIOD_PATH + ".total";

    private static final String CREDITORS_AFTER_CURRENT_PERIOD_DETAILS_PATH =
        CREDITORS_AFTER_CURRENT_PERIOD_PATH + ".details";

    private CompanyService companyService;

    private CurrentPeriodService currentPeriodService;

    private PreviousPeriodService previousPeriodService;

    @Autowired
    public CreditorsAfterOneYearValidator(CompanyService companyService, CurrentPeriodService currentPeriodService,
                                          PreviousPeriodService previousPeriodService) {

        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public Errors validateCreditorsAfterOneYear(@Valid CreditorsAfterOneYear creditorsAfterOneYear,
                                                Transaction transaction, String companyAccountId,
                                                HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        crossValidate(errors, request, companyAccountId, creditorsAfterOneYear);

        if (creditorsAfterOneYear.getCurrentPeriod() != null) {
            CurrentPeriod creditorsAfterCurrentPeriod = creditorsAfterOneYear.getCurrentPeriod();
            validateCurrentPeriod(creditorsAfterCurrentPeriod, errors);
        }

        if (creditorsAfterOneYear.getPreviousPeriod() != null) {
            PreviousPeriod creditorsAfterPreviousPeriod = creditorsAfterOneYear.getPreviousPeriod();

            try {
                if (companyService.isMultipleYearFiler(transaction)) {
                    validatePreviousPeriod(creditorsAfterPreviousPeriod, errors);
                } else {
                    validateInconsistentFilings(creditorsAfterPreviousPeriod, errors);
                }
            } catch (ServiceException se) {
                throw new DataException(se.getMessage(), se);
            }
        }

        return errors;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Errors crossValidate(Errors errors, HttpServletRequest request, String companyAccountsId,
                                CreditorsAfterOneYear creditorsAfterOneYear) throws DataException {

        crossValidateCurrentPeriod(errors, request, companyAccountsId, creditorsAfterOneYear);
        crossValidatePreviousPeriod(errors, request, companyAccountsId, creditorsAfterOneYear);

        return errors;
    }

    private void crossValidateCurrentPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
                                            CreditorsAfterOneYear creditorsAfterOneYear) throws DataException {

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request, companyAccountsId);

        checkIfCurrentNoteIsNullAndBalanceNot(errors, creditorsAfterOneYear, currentPeriodBalanceSheet);
        checkIsCurrentBalanceNullAndNoteNot(errors, creditorsAfterOneYear, currentPeriodBalanceSheet);
        checkIfCurrentBalanceAndNoteValuesAreEqual(errors, creditorsAfterOneYear, currentPeriodBalanceSheet);
    }

    private void crossValidatePreviousPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
                                             CreditorsAfterOneYear creditorsAfterOneYear) throws DataException {

        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request, companyAccountsId);

        checkIfPreviousNoteIsNullAndBalanceNot(errors, creditorsAfterOneYear, previousPeriodBalanceSheet);
        checkIsPreviousBalanceNullAndNoteNot(errors, creditorsAfterOneYear, previousPeriodBalanceSheet);
        checkIfPreviousBalanceAndNoteValuesAreEqual(errors, creditorsAfterOneYear, previousPeriodBalanceSheet);
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear,
                                                             BalanceSheet previousPeriodBalanceSheet) {

        if (!isPreviousPeriodBalanceSheetCreditorsAfterOneYearNull(previousPeriodBalanceSheet) &&
            (!isCreditorsAfterOneYearNotePreviousTotalNull(creditorsAfterOneYear)) &&
            (!creditorsAfterOneYear.getPreviousPeriod().getTotal().equals(
                previousPeriodBalanceSheet.getOtherLiabilitiesOrAssets().getCreditorsAfterOneYear()))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear,
                                                      BalanceSheet previousPeriodBalanceSheet) {

        if (isPreviousPeriodBalanceSheetCreditorsAfterOneYearNull(previousPeriodBalanceSheet) &&
            (!isCreditorsAfterOneYearNotePreviousTotalNull(creditorsAfterOneYear))) {

            addError(errors, previousBalanceSheetNotEqual, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear,
                                                        BalanceSheet previousPeriodBalanceSheet) {

        if (isCreditorsAfterOneYearNotePreviousTotalNull(creditorsAfterOneYear) &&
            (!isPreviousPeriodBalanceSheetCreditorsAfterOneYearNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private boolean isCreditorsAfterOneYearNotePreviousTotalNull(CreditorsAfterOneYear creditorsAfterOneYear) {
        return creditorsAfterOneYear.getPreviousPeriod() == null ||
            creditorsAfterOneYear.getPreviousPeriod().getTotal() == null;
    }

    private boolean isPreviousPeriodBalanceSheetCreditorsAfterOneYearNull(BalanceSheet previousPeriodBalanceSheet) {
        return previousPeriodBalanceSheet == null ||
            previousPeriodBalanceSheet.getOtherLiabilitiesOrAssets() == null ||
            previousPeriodBalanceSheet.getOtherLiabilitiesOrAssets().getCreditorsAfterOneYear() == null;
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        return Optional.ofNullable(
            currentPeriodService.findById(currentPeriodService.generateID(companyAccountsId), request))
            .map(ResponseObject::getData)
            .map(uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod::getBalanceSheet)
            .orElse(null);
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(HttpServletRequest request,
                                                       String companyAccountsId) throws DataException {

        return Optional.ofNullable(
            previousPeriodService.findById(previousPeriodService.generateID(companyAccountsId), request))
            .map(ResponseObject::getData)
            .map(uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod::getBalanceSheet)
            .orElse(null);
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear,
                                                            BalanceSheet currentPeriodBalanceSheet) {

        if (!isCurrentPeriodBalanceSheetCreditorsAfterOneYearNull(currentPeriodBalanceSheet) &&
            (!isCreditorsAfterOneYearNoteCurrentTotalNull(creditorsAfterOneYear)) &&
            (!creditorsAfterOneYear.getCurrentPeriod().getTotal().equals(
                currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets().getCreditorsAfterOneYear()))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsCurrentBalanceNullAndNoteNot(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear,
                                                     BalanceSheet currentPeriodBalanceSheet) {

        if (isCurrentPeriodBalanceSheetCreditorsAfterOneYearNull(currentPeriodBalanceSheet) &&
            (!isCreditorsAfterOneYearNoteCurrentTotalNull(creditorsAfterOneYear))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceNot(Errors errors, CreditorsAfterOneYear creditorsAfterOneYear, 
                                                       BalanceSheet currentPeriodBalanceSheet) {

        if (isCreditorsAfterOneYearNoteCurrentTotalNull(creditorsAfterOneYear) &&
            (!isCurrentPeriodBalanceSheetCreditorsAfterOneYearNull(currentPeriodBalanceSheet))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private boolean isCreditorsAfterOneYearNoteCurrentTotalNull(CreditorsAfterOneYear creditorsAfterOneYear) {
        return creditorsAfterOneYear.getCurrentPeriod() == null ||
            creditorsAfterOneYear.getCurrentPeriod().getTotal() == null;
    }

    private boolean isCurrentPeriodBalanceSheetCreditorsAfterOneYearNull(BalanceSheet currentPeriodBalanceSheet) {
        return currentPeriodBalanceSheet == null ||
            currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets() == null ||
            currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets().getCreditorsAfterOneYear() == null;
    }

    private void validateInconsistentFilings(PreviousPeriod creditorsAfterPreviousPeriod, Errors errors) {

        if (creditorsAfterPreviousPeriod.getTotal() != null) {
            addInconsistentDataError(errors, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validatePreviousPeriod(PreviousPeriod creditorsAfterPreviousPeriod, Errors errors) {

        if (creditorsAfterPreviousPeriod.getTotal() == null) {
            addErrorIfOtherPreviousPeriodFieldsProvided(creditorsAfterPreviousPeriod, errors);
        } else {
            validatePreviousTotalCalculationCorrect(creditorsAfterPreviousPeriod, errors);
        }
    }

    private void validatePreviousTotalCalculationCorrect(PreviousPeriod creditorsAfterPreviousPeriod, Errors errors) {

        Long bankLoansAndOverdrafts =
            Optional.ofNullable(creditorsAfterPreviousPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long financeLeasesAndHirePurchaseContracts =
            Optional.ofNullable(creditorsAfterPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long otherCreditors =
            Optional.ofNullable(creditorsAfterPreviousPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsAfterPreviousPeriod.getTotal();
        Long sum = bankLoansAndOverdrafts + financeLeasesAndHirePurchaseContracts + otherCreditors;

        validateAggregateTotal(total, sum, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH, errors);
    }

    private void addErrorIfOtherPreviousPeriodFieldsProvided(PreviousPeriod creditorsAfterPreviousPeriod, Errors errors) {

        if (creditorsAfterPreviousPeriod.getBankLoansAndOverdrafts() != null ||
            creditorsAfterPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
            creditorsAfterPreviousPeriod.getOtherCreditors() != null) {

            addError(errors, invalidNote, CREDITORS_AFTER_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validateCurrentPeriod(CurrentPeriod creditorsAfterCurrentPeriod, Errors errors) {

        if (creditorsAfterCurrentPeriod.getTotal() == null) {
            addErrorIfOtherCurrentPeriodFieldsProvided(creditorsAfterCurrentPeriod, errors);
            addErrorIfDetailsNotProvided(creditorsAfterCurrentPeriod, errors);
        } else {
            validateCurrentPeriodTotalCalculation(creditorsAfterCurrentPeriod, errors);
        }
    }

    private void validateCurrentPeriodTotalCalculation(CurrentPeriod creditorsAfterCurrentPeriod, Errors errors) {

        Long bankLoansAndOverdrafts =
            Optional.ofNullable(creditorsAfterCurrentPeriod.getBankLoansAndOverdrafts()).orElse(0L);
        Long financeLeasesAndHirePurchaseContracts =
            Optional.ofNullable(creditorsAfterCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
        Long otherCreditors =
            Optional.ofNullable(creditorsAfterCurrentPeriod.getOtherCreditors()).orElse(0L);

        Long total = creditorsAfterCurrentPeriod.getTotal();
        Long sum = bankLoansAndOverdrafts + financeLeasesAndHirePurchaseContracts + otherCreditors;

        validateAggregateTotal(total, sum, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH, errors);
    }

    private void addErrorIfDetailsNotProvided(CurrentPeriod creditorsAfterCurrentPeriod, Errors errors) {

        if (creditorsAfterCurrentPeriod.getDetails() == null) {
            addError(errors, invalidNote, CREDITORS_AFTER_CURRENT_PERIOD_DETAILS_PATH);
        }
    }

    private void addErrorIfOtherCurrentPeriodFieldsProvided(CurrentPeriod creditorsAfterCurrentPeriod, Errors errors) {

        if (creditorsAfterCurrentPeriod.getBankLoansAndOverdrafts() != null ||
            creditorsAfterCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
            creditorsAfterCurrentPeriod.getOtherCreditors() != null ||
            creditorsAfterCurrentPeriod.getDetails() != null) {

            addError(errors, invalidNote, CREDITORS_AFTER_CURRENT_PERIOD_TOTAL_PATH);
        }
    }
}
