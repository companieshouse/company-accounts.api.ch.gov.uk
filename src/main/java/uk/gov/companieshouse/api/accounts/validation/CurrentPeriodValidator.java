package uk.gov.companieshouse.api.accounts.validation;


import java.util.Optional;
import javax.validation.Valid;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;

/**
 * Validates Current Period
 */
@Component
public class CurrentPeriodValidator extends BaseValidator {

    private static final String BALANCE_SHEET_PATH = "$.current_period.balance_sheet";
    private static final String FIXED_ASSETS_PATH = BALANCE_SHEET_PATH + ".fixed_assets";
    private static final String FIXED_ASSETS_TOTAL_PATH = FIXED_ASSETS_PATH + ".total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH + ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";

    public Errors validateCurrentPeriod(@Valid CurrentPeriod currentPeriod) {

        Errors errors = new Errors();

        validateTotalFixedAssets(currentPeriod, errors);

        validateTotalOtherLiabilitiesOrAssets(currentPeriod, errors);

        validateTotalCurrentAssets(currentPeriod, errors);


        return errors;
    }

    public void validateTotalCurrentAssets(CurrentPeriod currentPeriod, Errors errors) {

        CurrentAssets currentAssets = currentPeriod.getBalanceSheet().getCurrentAssets();

        if (currentAssets != null) {

            Long stocks = Optional.ofNullable(currentAssets.getStocks()).orElse(0L);
            Long debtors = Optional.ofNullable(currentAssets.getDebtors()).orElse(0L);
            Long cashAtBankAndInHand = Optional.ofNullable(currentAssets.getCashAtBankAndInHand()).orElse(0L);
            Long currentAssetsTotal = Optional.ofNullable(currentAssets.getTotal()).orElse(0L);

            Long calculatedTotal = stocks + debtors + cashAtBankAndInHand;

            validateAggregateTotal(currentAssetsTotal, calculatedTotal, CURRENT_ASSETS_TOTAL_PATH, errors);
        }
    }

    public void validateTotalFixedAssets(@Valid CurrentPeriod currentPeriod, Errors errors) {
        FixedAssets fixedAssets = currentPeriod.getBalanceSheet().getFixedAssets();
        if (fixedAssets != null) {

            Long tangible = fixedAssets.getTangible();
            Long fixedAssetsTotal = fixedAssets.getTotal();

            // Will calculate the total of all fixedassets fields as they are added to the balance sheet
            Long calculatedTotal = tangible;

            validateAggregateTotal(fixedAssetsTotal, calculatedTotal, FIXED_ASSETS_TOTAL_PATH, errors);
        }
    }

    private void validateTotalOtherLiabilitiesOrAssets(@Valid CurrentPeriod currentPeriod, Errors errors) {
        if (currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets() != null) {
            calculateOtherLiabilitiesOrAssetsNetCurrentAssets(currentPeriod, errors);
            calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(currentPeriod, errors);
            calculateOtherLiabilitiesOrAssetsTotalNetAssets(currentPeriod, errors);
        }
    }

    private void calculateOtherLiabilitiesOrAssetsNetCurrentAssets(CurrentPeriod currentPeriod, Errors errors) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();
        Long prepaymentsAndAccruedIncome = Optional.ofNullable(otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome()).orElse(0L);
        Long creditorsDueWithinOneYear = Optional.ofNullable(otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear()).orElse(0L);

        Long totalCurrentAssets = 0L;
        if (currentPeriod.getBalanceSheet().getCurrentAssets() != null) {
            totalCurrentAssets = currentPeriod.getBalanceSheet().getCurrentAssets().getTotal();
        }
        Long calculatedTotal = totalCurrentAssets + prepaymentsAndAccruedIncome - creditorsDueWithinOneYear;

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);
        validateAggregateTotal(netCurrentAssets, calculatedTotal, OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH, errors);
    }

    private void calculateOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilities(CurrentPeriod currentPeriod, Errors errors) {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = currentPeriod.getBalanceSheet().getOtherLiabilitiesOrAssets();

        Long netCurrentAssets = Optional.ofNullable(otherLiabilitiesOrAssets.getNetCurrentAssets()).orElse(0L);

        Long fixedAssetsTotal = 0L;
        if (currentPeriod.getBalanceSheet().getFixedAssets() != null) {
            fixedAssetsTotal = Optional.ofNullable(currentPeriod.getBalanceSheet().getFixedAssets().getTotal()).orElse(0L);
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