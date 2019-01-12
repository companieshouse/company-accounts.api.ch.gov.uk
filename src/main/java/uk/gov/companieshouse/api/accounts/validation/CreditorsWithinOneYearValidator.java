package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsWithinOneYear.CreditorsWithinOneYear;
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

                validateCurrentPeriod(creditorsWithinOneYear, errors);
            }

            if (creditorsWithinOneYear.getPreviousPeriod() != null) {

                try {

                    if (companyService.isMultipleYearFiler(transaction)) {

                        validatePreviousPeriod(creditorsWithinOneYear, errors);

                    } else {

                        if (creditorsWithinOneYear.getPreviousPeriod().getTotal() != null) {

                            addInconsistentDataError(errors,
                                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
                        }
                    }
                } catch (ServiceException e) {
                    throw new DataException(e.getMessage(), e);
                }
            }
        }
        return errors;
    }

    private void validatePreviousPeriod(@Valid CreditorsWithinOneYear creditorsWithinOneYear,
            Errors errors) {
        if (creditorsWithinOneYear.getPreviousPeriod().getTotal() != null) {

            Long bankLoans =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getBankLoansAndOverdrafts()).orElse(0L);
            Long amountsDueUnderFinance =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
            Long tradeCreditors =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getBankLoansAndOverdrafts()).orElse(0L);
            Long taxation =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getTaxationAndSocialSecurity()).orElse(0L);
            Long accruals =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getAccrualsAndDeferredIncome()).orElse(0L);
            Long otherCreditors =
                    Optional.ofNullable(creditorsWithinOneYear.getPreviousPeriod().getOtherCreditors()).orElse(0L);

            Long total = creditorsWithinOneYear.getPreviousPeriod().getTotal();
            Long sum =
                    bankLoans + amountsDueUnderFinance + tradeCreditors + taxation + accruals + otherCreditors;

            validateAggregateTotal(total, sum,
                    CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH, errors);
        }


        // Validate total field present if numeric fields present
        if (creditorsWithinOneYear.getPreviousPeriod() != null) {

            if ((creditorsWithinOneYear.getPreviousPeriod().getBankLoansAndOverdrafts() != null ||
                    creditorsWithinOneYear.getPreviousPeriod().getFinanceLeasesAndHirePurchaseContracts() != null ||
                    creditorsWithinOneYear.getPreviousPeriod().getTradeCreditors() != null ||
                    creditorsWithinOneYear.getPreviousPeriod().getTaxationAndSocialSecurity() != null ||
                    creditorsWithinOneYear.getPreviousPeriod().getAccrualsAndDeferredIncome() != null ||
                    creditorsWithinOneYear.getPreviousPeriod().getOtherCreditors() != null) &&
                    creditorsWithinOneYear.getPreviousPeriod().getTotal() == null) {

                addError(errors, invalidNote, CREDITORS_WITHIN_PREVIOUS_PERIOD_TOTAL_PATH);
            }
        }
    }

    private void validateCurrentPeriod(@Valid CreditorsWithinOneYear creditorsWithinOneYear,
            Errors errors) {
        // Validate total field present if numeric fields present
        if ((creditorsWithinOneYear.getCurrentPeriod().getBankLoansAndOverdrafts() != null ||
                creditorsWithinOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts() != null ||
                creditorsWithinOneYear.getCurrentPeriod().getTradeCreditors() != null ||
                creditorsWithinOneYear.getCurrentPeriod().getTaxationAndSocialSecurity() != null ||
                creditorsWithinOneYear.getCurrentPeriod().getAccrualsAndDeferredIncome() != null ||
                creditorsWithinOneYear.getCurrentPeriod().getOtherCreditors() != null) &&
                creditorsWithinOneYear.getCurrentPeriod().getTotal() == null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH);
        }

        // Validate total calculations
        if (creditorsWithinOneYear.getCurrentPeriod().getTotal() != null) {

            Long bankLoans =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getBankLoansAndOverdrafts()).orElse(0L);
            Long amountsDueUnderFinance =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts()).orElse(0L);
            Long tradeCreditors =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getBankLoansAndOverdrafts()).orElse(0L);
            Long taxation =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getTaxationAndSocialSecurity()).orElse(0L);
            Long accruals =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getAccrualsAndDeferredIncome()).orElse(0L);
            Long otherCreditors =
                    Optional.ofNullable(creditorsWithinOneYear.getCurrentPeriod().getOtherCreditors()).orElse(0L);

            Long total = creditorsWithinOneYear.getCurrentPeriod().getTotal();
            Long sum =
                    bankLoans + amountsDueUnderFinance + tradeCreditors + taxation + accruals + otherCreditors;

            validateAggregateTotal(total, sum, CREDITORS_WITHIN_CURRENT_PERIOD_TOTAL_PATH
                    , errors);
        }


        // If total field not present validate additional info is present
        if (creditorsWithinOneYear.getCurrentPeriod().getBankLoansAndOverdrafts() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getFinanceLeasesAndHirePurchaseContracts() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getTradeCreditors() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getTaxationAndSocialSecurity() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getAccrualsAndDeferredIncome() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getOtherCreditors() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getTotal() == null &&
                creditorsWithinOneYear.getCurrentPeriod().getDetails() == null) {

            addError(errors, invalidNote, CREDITORS_WITHIN_CURRENT_PERIOD_DETAILS_PATH);

        }
    }
}
