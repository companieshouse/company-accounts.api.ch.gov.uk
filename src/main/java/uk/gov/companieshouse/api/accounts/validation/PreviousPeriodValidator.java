package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import javax.validation.Valid;

@Component
public class PreviousPeriodValidator extends BaseValidator {

    String PREVIOUS_PERIOD_PATH = "$.previous_period";
    String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    String TOTAL_PATH = FIXED_ASSETS_PATH + ".total";

    public Errors validatePreviousPeriod(
        @Valid PreviousPeriod previousPeriod, Errors errors) {

        if (previousPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(previousPeriod, errors);
        }

        return errors;
    }

    private void validateTotalFixedAssets(@Valid PreviousPeriod previousPeriod, Errors errors) {
        FixedAssets fixedAssets = previousPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Integer tangible = fixedAssets.getTangible();
            Integer fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            // Will calculate the total of all fixedAssets fields as they are added to the balance sheet
            Integer calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, TOTAL_PATH, errors);
        }
    }
}
