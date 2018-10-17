package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import javax.validation.Valid;

@Component
public class PreviousPeriodValidator extends BaseValidator {

    private static String PREVIOUS_PERIOD_PATH = "$.previous_period";
    private static String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    private static String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static String FIXED_ASSETS_TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";

    public Errors validatePreviousPeriod(
        @Valid PreviousPeriod previousPeriod) {

        Errors errors = new Errors();

        if (previousPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(previousPeriod, errors);
            validateTotalCurrentAssets(previousPeriod, errors);
        }

        return errors;
    }

    public void validateTotalCurrentAssets(PreviousPeriod previousPeriod, Errors errors) {

        CurrentAssets currentAssets = previousPeriod.getBalanceSheet().getCurrentAssets();

        Long stocks = currentAssets.getStocks();
        Long debtors = currentAssets.getDebtors();
        Long cashAtBandAndInHand = currentAssets.getCashAtBankAndInHand();
        Long currentAssetsTotal = currentAssets.getTotalCurrentAssets();

        Long calculatedTotal = 0L;

        calculatedTotal += stocks == null ? 0L : stocks;
        calculatedTotal += debtors == null ? 0L : debtors;
        calculatedTotal += cashAtBandAndInHand == null ? 0L : cashAtBandAndInHand;

        validateAggregateTotal(currentAssetsTotal, calculatedTotal, CURRENT_ASSETS_TOTAL_PATH, errors);

    }

    public void validateTotalFixedAssets(@Valid PreviousPeriod previousPeriod, Errors errors) {
        FixedAssets fixedAssets = previousPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Long tangible = fixedAssets.getTangible();
            Long fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            // Will calculate the total of all fixedAssets fields as they are added to the balance sheet
            Long calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, FIXED_ASSETS_TOTAL_PATH, errors);
        }
    }
}
