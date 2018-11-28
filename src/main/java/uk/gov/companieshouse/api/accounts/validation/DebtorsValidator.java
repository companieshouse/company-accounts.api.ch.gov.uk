package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyProfile;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

import java.util.Optional;

@Component
public class DebtorsValidator extends BaseValidator {

    private static String DEBTORS_PATH = "$.debtors";
    private static String TOTAL_PATH = DEBTORS_PATH + ".date";

    public Errors validateDebtors(Debtors debtors, Transaction transaction) {

        Errors errors = new Errors();

        if (debtors != null) {
            isMultipleYearFiler(transaction);
            validateTotalFieldNotNullWhenRequired(debtors, errors);
            validateTotalIsCorrect(debtors, errors);

        }


        return errors;
    }


    private static boolean isMultipleYearFiler(Transaction transaction) {

        // Get company number from transaction
        String companyNumber = transaction.getCompanyNumber();

        // Check if company profile has last accounts
        final String uri = "http://chs-dev:4061/company/" + companyNumber;

        RestTemplate restTemplate = new RestTemplate();
        CompanyProfile companyProfile = restTemplate.getForObject(uri, CompanyProfile.class);

        if (companyProfile.getAccounts().getLastAccounts() != null) {
            return true;

        }
        return false;
    }


    // Total mandatory if other fields entered
    private void validateTotalFieldNotNullWhenRequired(Debtors debtors, Errors errors) {
        if (debtors.getTradeDebtors() != null || debtors.getPrepaymentsAndAccruedIncome() != null ||
                debtors.getOtherDebtors() != null || debtors.getGreaterThanOneYear() != null || !debtors.getDetails().isEmpty()) {
            if (debtors.getTotal() == null) {
                addError(errors, "invalid_note", TOTAL_PATH);
            }
        }
    }

    // Total sum is correct
    private void validateTotalIsCorrect(Debtors debtors, Errors errors) {

        if (debtors.getTotal() != null) {
            Long traderDebtors = Optional.ofNullable(debtors.getTradeDebtors()).orElse(0L);
            Long prepayments = Optional.ofNullable(debtors.getPrepaymentsAndAccruedIncome()).orElse(0L);
            Long otherDebtors = Optional.ofNullable(debtors.getOtherDebtors()).orElse(0L);
            Long moreThanOneYear = Optional.ofNullable(debtors.getGreaterThanOneYear()).orElse(0L);

            Long total = debtors.getTotal();

            Long sum = traderDebtors + prepayments + otherDebtors + moreThanOneYear;

            validateAggregateTotal(total, sum, TOTAL_PATH, errors);

        }
    }


}


    // Previous period if first year filer

