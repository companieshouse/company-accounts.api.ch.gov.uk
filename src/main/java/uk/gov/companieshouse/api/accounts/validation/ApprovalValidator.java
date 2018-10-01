package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.Valid;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


/**
 * Validates Current Period
 */
@Component
public class ApprovalValidator extends BaseValidator {

    String CURRENT_PERIOD_PATH = "$.current_period";
    String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    String TOTAL_PATH = FIXED_ASSETS_PATH + ".total";

    public Errors validateApproval(@Valid Approval approval, Errors errors) {



        return errors;
    }

    private void validateTotalFixedAssets(@Valid CurrentPeriod currentPeriod,
        Errors errors) {
        FixedAssets fixedAssets = currentPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Integer tangible = fixedAssets.getTangible();
            Integer fixedAssetsTotal = fixedAssets.getTotalFixedAssets();

            // Will calculate the total of all fixedAssets fields as they are added to the balance sheet
            Integer calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, TOTAL_PATH, errors);
        }
    }
}