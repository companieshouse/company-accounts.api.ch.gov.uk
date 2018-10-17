package uk.gov.companieshouse.api.accounts.validation;


import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;

import javax.validation.Valid;

/**
 * Validates Current Period
 */
@Component
public class CurrentPeriodValidator extends BaseValidator {

    private static String CURRENT_PERIOD_PATH = "$.current_period";
    private static String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    private static String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static String FIXED_ASSETS_TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static String CURRENT_ASSETS_TOTAL_PATH = "$.current_period.balance_sheet.current_assets.total";

    public Errors validateCurrentPeriod(@Valid CurrentPeriod currentPeriod) {

        Errors errors = new Errors();

        if (currentPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(currentPeriod, errors);

            validateTotalCurrentAssets(currentPeriod, errors);
        }

        return errors;
    }

    public void validateTotalCurrentAssets(CurrentPeriod currentPeriod, Errors errors) {

        CurrentAssets currentAssets = currentPeriod.getBalanceSheet().getCurrentAssets();

        Long stocks = currentAssets.getStocks();
        Long debtors = currentAssets.getDebtors();
        Long cashAtBandAndInHand = currentAssets.getCashAtBankAndInHand();
        Long currentAssetsTotal = currentAssets.getTotalCurrentAssets();

        Long calculatedTotal = 0L;

        calculatedTotal += stocks == null ? 0L : stocks;
        calculatedTotal += debtors == null ? 0L : debtors;
        calculatedTotal += cashAtBandAndInHand == null ? 0L : cashAtBandAndInHand;

        validateAggregateTotal(currentAssetsTotal, calculatedTotal, CURRENT_ASSETS_TOTAL_PATH, errors );

    }

    public void validateTotalFixedAssets(@Valid CurrentPeriod currentPeriod,
        Errors errors) {
        FixedAssets fixedAssets = currentPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Long tangible = fixedAssets.getTangible();
            Long fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            // Will calculate the total of all fixedAssets fields as they are added to the balance sheet
            Long calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, FIXED_ASSETS_TOTAL_PATH, errors);
        }
    }
}