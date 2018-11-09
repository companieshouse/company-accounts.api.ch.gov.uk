package uk.gov.companieshouse.api.accounts.validation;


import org.springframework.stereotype.Component;
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
    private static String TOTAL_PATH = FIXED_ASSETS_PATH + ".total";

    public Errors validateCurrentPeriod(@Valid CurrentPeriod currentPeriod) {

        Errors errors = new Errors();

        if (currentPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(currentPeriod, errors);
        }

        return errors;
    }
    
    private void validateTotalFixedAssets(@Valid CurrentPeriod currentPeriod,
        Errors errors) {
        FixedAssets fixedAssets = currentPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Long tangible = fixedAssets.getTangible();
            Long fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            // Will calculate the total of all fixedAssets fields as they are added to the balance sheet
            Long calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, TOTAL_PATH, errors);
        }
    }
}