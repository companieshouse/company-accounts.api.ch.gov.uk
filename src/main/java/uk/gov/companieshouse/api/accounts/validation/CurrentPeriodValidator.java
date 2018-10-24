package uk.gov.companieshouse.api.accounts.validation;


import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Validates Current Period
 */
@Component
public class CurrentPeriodValidator extends BaseValidator {

    private static String CURRENT_PERIOD_PATH = "$.current_period";
    private static String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    private static String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static String FIXED_ASSETS_TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total_current_assets";

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

        Long stocks = Optional.ofNullable(currentAssets.getStocks()).orElse(0L);
        Long debtors = Optional.ofNullable(currentAssets. getDebtors()).orElse(0L);
        Long cashAtBankAndInHand = Optional.ofNullable(currentAssets.getCashAtBankAndInHand()).orElse(0L);
        Long currentAssetsTotal = Optional.ofNullable(currentAssets.getTotalCurrentAssets()).orElse(0L);

        Long calculatedTotal = stocks + debtors + cashAtBankAndInHand;

        validateAggregateTotal(currentAssetsTotal, calculatedTotal, CURRENT_ASSETS_TOTAL_PATH, errors);
    }

    public void validateTotalFixedAssets(@Valid CurrentPeriod currentPeriod, Errors errors) {
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