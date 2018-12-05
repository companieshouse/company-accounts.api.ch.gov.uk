package uk.gov.companieshouse.api.accounts.validation;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyProfile;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Component
public class DebtorsValidator extends BaseValidator {

    @Value("${invalid.note}")
    private String invalidNote;

    private static String DEBTORS_PATH = "$.debtors";
    private static String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";

    private static String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static String PREVIOUS_TRADE_DEBTORS = DEBTORS_PATH_PREVIOUS + ".trade_debtors";
    private static String PREVIOUS_PREPAYMENTS = DEBTORS_PATH_PREVIOUS + ".prepayments_and_accrued_income";
    private static String PREVIOUS_OTHER_DEBTORS = DEBTORS_PATH_PREVIOUS + ".other_debtors";
    private static String PREVIOUS_GREATER_THAN_ONE_YEAR = DEBTORS_PATH_PREVIOUS + ".greater_than_one_year";

    private static final String COMPANY_PROFILE_URL = "CHS_COMPANY_PROFILE_API_LOCAL_URL";

    @Autowired
    EnvironmentReader environmentReader;


    public Errors validateDebtors(@Valid Debtors debtors, Transaction transaction) {

        Errors errors = new Errors();

        if (debtors != null) {

            if (debtors.getCurrentPeriod() != null) {

                validateCurrentPeriodDebtors(errors, debtors);
            }

            if (debtors.getPreviousPeriod() != null) {
                if (isMultipleYearFiler(transaction)) {

                    validatePreviousPeriodDebtors(errors, debtors, transaction);

                } else validateInconsistentPeriodFiling(debtors, transaction, errors);

            }
        }
        return errors;
    }

        private void validatePreviousPeriodDebtors (Errors errors, Debtors debtors, Transaction
        transaction){

            validateRequiredPreviousPeriodTotalFieldNotNull(debtors, errors);
            validatePreviousPeriodTotalIsCorrect(debtors, errors);
        }

        private void addInconsistentDataError (Errors errors, String errorPath){

            addError(errors, "inconsistent_data", errorPath);
        }

        private void validateCurrentPeriodDebtors (Errors errors, Debtors debtors){

            validateRequiredCurrentPeriodTotalFieldNotNull(debtors, errors);
            validateCurrentPeriodTotalIsCorrect(debtors, errors);
        }

        private void validateRequiredCurrentPeriodTotalFieldNotNull (Debtors debtors, Errors errors)
        {

            if (debtors.getCurrentPeriod().getTradeDebtors() != null ||
                debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome() != null ||
                debtors.getCurrentPeriod().getOtherDebtors() != null ||
                debtors.getCurrentPeriod().getGreaterThanOneYear() != null || !debtors.getCurrentPeriod().getDetails().isEmpty()) {

                if (debtors.getCurrentPeriod().getTotal() == null) {

                    addError(errors, "invalid_note", CURRENT_TOTAL_PATH);
                }
            }
        }

        private void validateRequiredPreviousPeriodTotalFieldNotNull (Debtors debtors, Errors errors)
        {

            if (debtors.getPreviousPeriod().getTradeDebtors() != null ||
                debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome() != null ||
                debtors.getPreviousPeriod().getOtherDebtors() != null ||
                debtors.getPreviousPeriod().getGreaterThanOneYear() != null) {

                if (debtors.getPreviousPeriod().getTotal() == null) {

                    addError(errors, "invalid_note", PREVIOUS_TOTAL_PATH);
                }
            }
        }

        private void validateCurrentPeriodTotalIsCorrect (Debtors debtors, Errors errors){

            if (debtors.getCurrentPeriod().getTotal() != null) {
                Long traderDebtors = Optional.ofNullable(debtors.getCurrentPeriod().getTradeDebtors()).orElse(0L);
                Long prepayments = Optional.ofNullable(debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome()).orElse(0L);
                Long otherDebtors = Optional.ofNullable(debtors.getCurrentPeriod().getOtherDebtors()).orElse(0L);
                Long moreThanOneYear = Optional.ofNullable(debtors.getCurrentPeriod().getGreaterThanOneYear()).orElse(0L);

                Long total = debtors.getCurrentPeriod().getTotal();

                Long sum = traderDebtors + prepayments + otherDebtors + moreThanOneYear;

                validateAggregateTotal(total, sum, CURRENT_TOTAL_PATH, errors);

            }
        }

        private void validatePreviousPeriodTotalIsCorrect (Debtors debtors, Errors errors){

            if (debtors.getPreviousPeriod().getTotal() != null) {
                Long traderDebtors = Optional.ofNullable(debtors.getPreviousPeriod().getTradeDebtors()).orElse(0L);
                Long prepayments = Optional.ofNullable(debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome()).orElse(0L);
                Long otherDebtors = Optional.ofNullable(debtors.getPreviousPeriod().getOtherDebtors()).orElse(0L);
                Long moreThanOneYear = Optional.ofNullable(debtors.getPreviousPeriod().getGreaterThanOneYear()).orElse(0L);

                Long total = debtors.getPreviousPeriod().getTotal();

                Long sum = traderDebtors + prepayments + otherDebtors + moreThanOneYear;

                validateAggregateTotal(total, sum, PREVIOUS_TOTAL_PATH, errors);

            }
        }


        public boolean isMultipleYearFiler (Transaction transaction){


            String companyNumber = transaction.getCompanyNumber();
            String companyProfileUrl = environmentReader.getMandatoryString(COMPANY_PROFILE_URL);

            final String uri = companyProfileUrl + "/company/" + companyNumber;

            RestTemplate restTemplate = new RestTemplate();

            CompanyProfile companyProfile = restTemplate.getForObject(uri, CompanyProfile.class);


            if (companyProfile.getAccounts().getLastAccounts() != null) {
                return true;

            }
            return false;
        }

        private void validateInconsistentPeriodFiling (Debtors debtors, Transaction
        transaction, Errors errors){

            if (debtors.getPreviousPeriod().getTradeDebtors() != null) {
                addInconsistentDataError(errors, PREVIOUS_TRADE_DEBTORS);
            }

            if (debtors.getPreviousPeriod().getGreaterThanOneYear() != null) {
                addInconsistentDataError(errors, PREVIOUS_GREATER_THAN_ONE_YEAR);
            }

            if (debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome() != null) {
                addInconsistentDataError(errors, PREVIOUS_PREPAYMENTS);
            }

            if (debtors.getPreviousPeriod().getOtherDebtors() != null) {
                addInconsistentDataError(errors, PREVIOUS_OTHER_DEBTORS);
            }

            if (debtors.getPreviousPeriod().getTotal() != null) {
                addInconsistentDataError(errors, PREVIOUS_TOTAL_PATH);
            }
        }
    }


