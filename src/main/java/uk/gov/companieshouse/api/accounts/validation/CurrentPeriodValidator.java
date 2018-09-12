package uk.gov.companieshouse.api.accounts.validation;


import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.Errors;
import uk.gov.companieshouse.api.accounts.model.NumericRange;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;

import javax.validation.Valid;

/**
 * Validates Current Period
 */
@Component
public class CurrentPeriodValidator extends BaseValidator {

    String CURRENT_PERIOD_PATH = "$.current_period";
    String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    String TANGIBLE_PATH = FIXED_ASSETS_PATH + ".tangible";
    String TOTAL_PATH = FIXED_ASSETS_PATH + ".total";

    protected static final NumericRange ZERO_TO_99999999 = new NumericRange(0, 99999999);

    public Errors validateCurrentPeriod(@Valid CurrentPeriod currentPeriod) {

        Errors errors = new Errors();

        validateBalanceSheetFixedAssets(currentPeriod, errors);

        return errors;
    }

    private void validateBalanceSheetFixedAssets(@Valid CurrentPeriod currentPeriod,
        Errors errors) {
        FixedAssets fixedAssets = currentPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Integer tangible = fixedAssets.getTangible();
            Integer fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            Integer calculatedTotal = tangible;

            validateOptionalWithinRange(fixedAssets.getTangible(), ZERO_TO_99999999, errors,
                TANGIBLE_PATH);

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, TOTAL_PATH, errors);
        }
    }
}