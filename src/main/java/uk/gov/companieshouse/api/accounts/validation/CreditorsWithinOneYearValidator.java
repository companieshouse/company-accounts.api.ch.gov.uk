package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mongodb.MongoException;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class CreditorsWithinOneYearValidator extends BaseValidator implements CrossValidator<CreditorsWithinOneYear> {

    private static final String CREDITORS_WITHIN_PATH = "$.creditors_within_one_year";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".current_period";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".previous_period";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_ACCRUALS_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".accruals_and_deferred_income";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_BANK_LOANS_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".bank_loans_and_overdrafts";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_FINANCE_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".finance_leases_and_hire_purchase_contracts";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_OTHER_CREDITORS_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".other_creditors";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TAXATION_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".taxation_and_social_security";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TRADE_CREDITORS_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".trade_creditors";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".total";

    private CompanyService companyService;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public CreditorsWithinOneYearValidator(CompanyService companyService, CurrentPeriodService currentPeriodService,
        PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public Errors validateCreditorsWithinOneYear(@Valid CreditorsWithinOneYear creditorsWithinOneYear, Transaction transaction,
            String companyAccountsId,
            HttpServletRequest request) throws DataException {

        Errors errors = new Errors();
        
        crossValidate(errors, request, companyAccountsId, creditorsWithinOneYear);

        if (creditorsWithinOneYear.getCurrentPeriod() != null) {

            CurrentPeriod creditorsCurrentPeriod = creditorsWithinOneYear.getCurrentPeriod();

            validateCurrentPeriod(creditorsCurrentPeriod, errors);
        }

        if (creditorsWithinOneYear.getPreviousPeriod() != null) {

            PreviousPeriod creditorsPreviousPeriod = creditorsWithinOneYear.getPreviousPeriod();

            try {

                if (companyService.isMultipleYearFiler(transaction)) {

                    validatePreviousPeriod(creditorsPreviousPeriod, errors);

                } else {

                    validateInconsistentFiling(creditorsPreviousPeriod, errors);
                }
            } catch (ServiceException e) {
                throw new DataException(e.getMessage(), e);
            }
        }
        return errors;
    }

    private void validateInconsistentFiling(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {

        if (creditorsPreviousPeriod.getAccrualsAndDeferredIncome() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_ACCRUALS_PATH);
        }

        if (creditorsPreviousPeriod.getBankLoansAndOverdrafts() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_BANK_LOANS_PATH);
        }

        if (creditorsPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_FINANCE_PATH);
        }

        if (creditorsPreviousPeriod.getOtherCreditors() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_OTHER_CREDITORS_PATH);
        }

        if (creditorsPreviousPeriod.getTaxationAndSocialSecurity() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TAXATION_PATH);
        }

        if (creditorsPreviousPeriod.getTradeCreditors() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TRADE_CREDITORS_PATH);
        }

        if (creditorsPreviousPeriod.getTotal() != null) {
            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validatePreviousPeriod(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {

        if (creditorsPreviousPeriod.getTotal() == null) {

            addErrorIfOtherValuesProvided(creditorsPreviousPeriod, errors);
        } else {

            validatePreviousTotalCalculationCorrect(creditorsPreviousPeriod, errors);
        }
    }

    private void addErrorIfOtherValuesProvided(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {

        if (creditorsPreviousPeriod.getBankLoansAndOverdrafts() != null ||
                creditorsPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
                creditorsPreviousPeriod.getTradeCreditors() != null ||
                creditorsPreviousPeriod.getTaxationAndSocialSecurity() != null ||
                creditorsPreviousPeriod.getAccrualsAndDeferredIncome() != null ||
                creditorsPreviousPeriod.getOtherCreditors() != null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
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

    private void validateCurrentPeriod(@Valid CurrentPeriod creditorsCurrentPeriod,
            Errors errors) {

        if (creditorsCurrentPeriod.getTotal() == null) {

            addErrorIfOtherFieldsProvided(creditorsCurrentPeriod, errors);
        } else {

            validateCurrentPeriodTotalCalculation(creditorsCurrentPeriod, errors);

        }
    }

    private void addErrorIfOtherFieldsProvided(@Valid CurrentPeriod creditorsCurrentPeriod,
            Errors errors) {
        if (creditorsCurrentPeriod.getBankLoansAndOverdrafts() != null ||
                creditorsCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
                creditorsCurrentPeriod.getTradeCreditors() != null ||
                creditorsCurrentPeriod.getTaxationAndSocialSecurity() != null ||
                creditorsCurrentPeriod.getAccrualsAndDeferredIncome() != null ||
                creditorsCurrentPeriod.getOtherCreditors() != null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
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
    
    /**
     * @inheritDoc
     */

    @Override
    public Errors crossValidate (Errors errors, HttpServletRequest request,
            String companyAccountsId,
            CreditorsWithinOneYear creditorsWithinOneYear) throws DataException {

        crossValidateCurrentPeriod(errors, request, creditorsWithinOneYear, companyAccountsId);
        crossValidatePreviousPeriod(errors, request, creditorsWithinOneYear, companyAccountsId);

        return errors;
    }
    
    private void crossValidatePreviousPeriod (Errors errors, HttpServletRequest request,
            CreditorsWithinOneYear creditorsWithinOneYear, String companyAccountsId) throws DataException {

        BalanceSheet previousPeriodBalanceSheet =
                getPreviousPeriodBalanceSheet(request, companyAccountsId);

        checkIfPreviousNoteIsNullAndBalanceNot(errors, creditorsWithinOneYear, previousPeriodBalanceSheet);
        checkIsPrevousBalanceNullAndNoteNot(errors, creditorsWithinOneYear, previousPeriodBalanceSheet);
        checkIfPreviousBalanceAndNoteValuesAreEqual(errors, creditorsWithinOneYear, previousPeriodBalanceSheet);
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
        BalanceSheet previousPeriodBalanceSheet) {
        if (! isPreviousPeriodBalanceSheetCreditorsWithinOneYearNull(previousPeriodBalanceSheet) &&
                (! isCreditorsWithinOneYearNotePreviousTotalNull(creditorsWithinOneYear))
                && (! creditorsWithinOneYear.getPreviousPeriod().getTotal().equals(
                    previousPeriodBalanceSheet.getOtherLiabilitiesOrAssets()
                    .getCreditorsDueWithinOneYear()))) {

            addError(errors, previousBalanceSheetNotEqual, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPrevousBalanceNullAndNoteNot (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
        BalanceSheet previousPeriodBalanceSheet) {
        if (isPreviousPeriodBalanceSheetCreditorsWithinOneYearNull(previousPeriodBalanceSheet) &&
                (! isCreditorsWithinOneYearNotePreviousTotalNull(creditorsWithinOneYear))) {

            addError(errors, previousBalanceSheetNotEqual, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
        BalanceSheet previousPeriodBalanceSheet) {
        if (isCreditorsWithinOneYearNotePreviousTotalNull(creditorsWithinOneYear) &&
                (! isPreviousPeriodBalanceSheetCreditorsWithinOneYearNull(previousPeriodBalanceSheet))) {

            addError(errors, previousBalanceSheetNotEqual, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private BalanceSheet getPreviousPeriodBalanceSheet (
            HttpServletRequest request, String companyAccountsId) throws DataException {
        String previousPeriodId = previousPeriodService.generateID(companyAccountsId);

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject;

        try {
            previousPeriodResponseObject =
                    previousPeriodService.findById(previousPeriodId, request);
        } catch (MongoException e) {

            throw new DataException(e.getMessage(), e);
        }
        if(previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
          return previousPeriodResponseObject.getData().getBalanceSheet();
        } else {
          return null;
        }
    }

    private void crossValidateCurrentPeriod (Errors errors, HttpServletRequest request,
            CreditorsWithinOneYear creditorsWithinOneYear,
            String companyAccountsId) throws DataException {

        BalanceSheet currentPeriodBalanceSheet =
                getCurrentPeriodBalanceSheet(request, companyAccountsId);

        checkIfCurrentNoteIsNullAndBalanceSheetNot(errors, creditorsWithinOneYear, currentPeriodBalanceSheet);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(errors, creditorsWithinOneYear, currentPeriodBalanceSheet);
        checkIfCurrentBalanceAndNoteValuesAreEqual(errors, creditorsWithinOneYear, currentPeriodBalanceSheet);
    }

    private void checkIfCurrentBalanceAndNoteValuesAreEqual (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
        BalanceSheet currentPeriodBalanceSheet) {
        
        if (! (isCurrentPeriodBalanceSheetCreditorsWithinOneYearNull(currentPeriodBalanceSheet)) &&
                (! isCreditorsWithinOneYearNoteCurrentTotalNull(creditorsWithinOneYear))

                && !(creditorsWithinOneYear.getCurrentPeriod().getTotal().equals(currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets()
                    .getCreditorsDueWithinOneYear()))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
        BalanceSheet currentPeriodBalanceSheet) {
        if (isCurrentPeriodBalanceSheetCreditorsWithinOneYearNull(currentPeriodBalanceSheet) &&
                (! isCreditorsWithinOneYearNoteCurrentTotalNull(creditorsWithinOneYear))) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot (Errors errors, CreditorsWithinOneYear creditorsWithinOneYear,
            BalanceSheet currentPeriodBalanceSheet) {
        if (isCreditorsWithinOneYearNoteCurrentTotalNull(creditorsWithinOneYear) &&
            currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets()
                        .getCreditorsDueWithinOneYear() != null) {

            addError(errors, currentBalanceSheetNotEqual, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private BalanceSheet getCurrentPeriodBalanceSheet (HttpServletRequest request,
            String companyAccountsId) throws DataException {
        String currentPeriodId = currentPeriodService.generateID(companyAccountsId);

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject;

        try {

            currentPeriodResponseObject = currentPeriodService.findById(currentPeriodId, request);
        } catch (MongoException e) {

            throw new DataException(e.getMessage(), e);
        }
        if(currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
          return currentPeriodResponseObject.getData().getBalanceSheet();
        } else {
          return null;
        }
    }

    private boolean isCreditorsWithinOneYearNoteCurrentTotalNull (CreditorsWithinOneYear creditorsWithinOneYear) {
        return creditorsWithinOneYear.getCurrentPeriod() == null || creditorsWithinOneYear.getCurrentPeriod().getTotal() == null;
    }

    private boolean isCurrentPeriodBalanceSheetCreditorsWithinOneYearNull (
            BalanceSheet currentPeriodBalanceSheet) {

        return currentPeriodBalanceSheet == null ||
            currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets() == null ||
            currentPeriodBalanceSheet.getOtherLiabilitiesOrAssets().getCreditorsDueWithinOneYear() == null;
    }

    private boolean isPreviousPeriodBalanceSheetCreditorsWithinOneYearNull (
            BalanceSheet previousPeriodBlanceSheet) {

        return previousPeriodBlanceSheet == null ||
            previousPeriodBlanceSheet.getOtherLiabilitiesOrAssets() == null ||
            previousPeriodBlanceSheet.getOtherLiabilitiesOrAssets().getCreditorsDueWithinOneYear() == null;
    }

    private boolean isCreditorsWithinOneYearNotePreviousTotalNull (CreditorsWithinOneYear creditorsWithinOneYear) {

        return creditorsWithinOneYear.getPreviousPeriod() == null ||
            creditorsWithinOneYear.getPreviousPeriod().getTotal() == null;
    }
}
