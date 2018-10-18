package uk.gov.companieshouse.api.accounts.validation;


import java.util.Optional;
import javax.validation.Valid;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

/**
 * Validates Current Period
 */
@Component
public class CurrentPeriodValidator extends BaseValidator {

    private static final String CURRENT_PERIOD_PATH = "$.current_period";
    private static final String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    private static final String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static final String TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = BALANCE_SHEET_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = BALANCE_SHEET_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = BALANCE_SHEET_PATH + ".total_net_assets";

    public Errors validateCurrentPeriod(@Valid CurrentPeriod currentPeriod) {

        Errors errors = new Errors();

        if (currentPeriod.getBalanceSheet() != null) {

            validateTotalFixedAssets(currentPeriod, errors);

            validateTotalOtherLiabilitiesOrAssets(currentPeriod, errors);
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

    private void validateTotalOtherLiabilitiesOrAssets(@Valid CurrentPeriod currentPeriod, Errors errors) {
        if (currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {
            calculateOtherLiabilitiesOrAssetsNetCurrentAssets(currentPeriod, errors);
            calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(currentPeriod, errors);
            calculateOtherLiabilitiesOrAssetsTotalNetAssets(currentPeriod,errors);
        }
    }
    private void calculateOtherLiabilitiesOrAssetsNetCurrentAssets(CurrentPeriod currentPeriod, Errors errors) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();
        Long prepaymentsAndAccruedIncome = Optional.ofNullable(otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long creditorsDueWithinOneYear = Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear()).orElse(0L);

        Long calculatedTotal = /* current_assets.total_current_assets + */ prepaymentsAndAccruedIncome - creditorsDueWithinOneYear;

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);
        validateAggregateTotal(netCurrentAssets, calculatedTotal, OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(CurrentPeriod currentPeriod, Errors errors) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);

        Long fixedAssetsTotal = 0L;
        if (currentPeriod.getBalanceSheet().getFixedAssets() != null) {
            fixedAssetsTotal = Optional.ofNullable(currentPeriod.getBalanceSheet().getFixedAssets().getTotalFixedAssets()).orElse(0L);
        }
        Long calculatedTotal = fixedAssetsTotal + netCurrentAssets;

        Long totalAssetsLessCurrentLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        validateAggregateTotal(totalAssetsLessCurrentLiabilities, calculatedTotal, OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalNetAssets(CurrentPeriod currentPeriod, Errors errors) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();
        Long totalAssetsLessCurrentLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities()).orElse(0L);
        Long creditorsAfterOneYear = Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsAfterOneYear()).orElse(0L);
        Long accrualsAndDeferredIncome = Optional.ofNullable(otherLiabilitiesOrAssets.getAccrualsAndDeferredIncome()).orElse(0L);
        Long provisionForLiabilities = Optional.ofNullable(otherLiabilitiesOrAssets.getProvisionForLiabilities()).orElse(0L);

        Long calculatedTotal = totalAssetsLessCurrentLiabilities - (creditorsAfterOneYear + accrualsAndDeferredIncome + provisionForLiabilities);

        Long totalNetAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getTotalNetAssets()).orElse(0L);
        validateAggregateTotal(totalNetAssets, calculatedTotal, OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH, errors);
    }
}