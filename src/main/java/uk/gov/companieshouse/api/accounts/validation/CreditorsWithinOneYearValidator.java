package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class CreditorsWithinOneYearValidator extends BaseValidator {

    @Value("${invalid.note}")
    private String invalidNote;

    private static final String CREDITORS_WITHIN_PATH = "$.creditors_within_one_year";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".current_period";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH = CREDITORS_WITHIN_PATH +
            ".previous_period";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".total";
    private static final String CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH =
            CREDITORS_WITHIN_PREVIOUS_PERIOD_PATH + ".total";
    private static final String CREDITORS_WITHIN_CURRENT_PERIOD_DETAILS_PATH =
            CREDITORS_WITHIN_CURRENT_PERIOD_PATH + ".details";

    private CompanyService companyService;

    @Autowired
    public CreditorsWithinOneYearValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Errors validateCreditorsWithinOneYear(@Valid CreditorsWithinOneYear creditorsWithinOneYear, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        if (creditorsWithinOneYear != null) {

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
        }
        return errors;
    }

    private void validateInconsistentFiling(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {
        if (creditorsPreviousPeriod.getTotal() != null) {

            addInconsistentDataError(errors,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validatePreviousPeriod(@Valid PreviousPeriod creditorsPreviousPeriod,
            Errors errors) {
        validatePreviousTotalCalculationCorrect(creditorsPreviousPeriod, errors);
        validatePreviousTotalProvidedIfValuesEntered(creditorsPreviousPeriod, errors);
    }

    private void validatePreviousTotalProvidedIfValuesEntered(@Valid PreviousPeriod creditorsPreviousPeriod, Errors errors) {

        if ((creditorsPreviousPeriod.getBankLoansAndOverdrafts() != null ||
                creditorsPreviousPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
                creditorsPreviousPeriod.getTradeCreditors() != null ||
                creditorsPreviousPeriod.getTaxationAndSocialSecurity() != null ||
                creditorsPreviousPeriod.getAccrualsAndDeferredIncome() != null ||
                creditorsPreviousPeriod.getOtherCreditors() != null) &&
                (creditorsPreviousPeriod.getTotal() == null)) {

            addError(errors, invalidNote, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validatePreviousTotalCalculationCorrect(@Valid PreviousPeriod creditorsPreviousPeriod, Errors errors) {
        if (creditorsPreviousPeriod.getTotal() != null) {

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
    }

    private void validateCurrentPeriod(@Valid CurrentPeriod creditorsCurrentPeriod,
            Errors errors) {

        validateTotalFieldPresentIfCurrentValuesProvided(creditorsCurrentPeriod, errors);
        validateCurrentPeriodTotalCalculation(creditorsCurrentPeriod, errors);
        validateDetailsPresentIfNoTotalProvided(creditorsCurrentPeriod, errors);
    }

    private void validateTotalFieldPresentIfCurrentValuesProvided(@Valid CurrentPeriod creditorsCurrentPeriod, Errors errors) {
        if ((creditorsCurrentPeriod.getBankLoansAndOverdrafts() != null ||
                creditorsCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts() != null ||
                creditorsCurrentPeriod.getTradeCreditors() != null ||
                creditorsCurrentPeriod.getTaxationAndSocialSecurity() != null ||
                creditorsCurrentPeriod.getAccrualsAndDeferredIncome() != null ||
                creditorsCurrentPeriod.getOtherCreditors() != null) &&
                creditorsCurrentPeriod.getTotal() == null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void validateDetailsPresentIfNoTotalProvided(@Valid CurrentPeriod creditorsCurrentPeriod, Errors errors) {
        if (creditorsCurrentPeriod.getBankLoansAndOverdrafts() == null &&
                creditorsCurrentPeriod.getFinanceLeasesAndHirePurchaseContracts() == null &&
                creditorsCurrentPeriod.getTradeCreditors() == null &&
                creditorsCurrentPeriod.getTaxationAndSocialSecurity() == null &&
                creditorsCurrentPeriod.getAccrualsAndDeferredIncome() == null &&
                creditorsCurrentPeriod.getOtherCreditors() == null &&
                creditorsCurrentPeriod.getTotal() == null &&
                creditorsCurrentPeriod.getDetails() == null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_CURRENT_PERIOD_DETAILS_PATH);

        }
    }

    private void validateCurrentPeriodTotalCalculation(@Valid CurrentPeriod creditorsCurrentPeriod, Errors errors) {
        if (creditorsCurrentPeriod.getTotal() != null) {

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
    }
}
