package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;


public class PreviousPeriodValidatorTest {

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";
    private static final String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    private static final String FIXED_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = BALANCE_SHEET_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = BALANCE_SHEET_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = BALANCE_SHEET_PATH + ".total_net_assets";

    PreviousPeriodValidator validator = new PreviousPeriodValidator();

    PreviousPeriod previousPeriod = new PreviousPeriod();
    BalanceSheet balanceSheet = new BalanceSheet();

    @Test
    @DisplayName("SUCCESS - Test Balance Sheet validation")
    void validateBalanceSheet() {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(2L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(4L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotalFixedAssets(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("ERROR - Fixed Assets - Test validation with total fixed assets error")
    void validateBalanceSheetWithTotalFixedAssetsError() {
        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5L);
        fixedAssets.setTotalFixedAssets(10L);
        balanceSheet.setFixedAssets(fixedAssets);
        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("incorrect_total", FIXED_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with net current assets error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsNetCurrentAssetsError() {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");


        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with total assets less current liabilities error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsTotalAssetsLessCurrentLiabilitiesError() {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(2L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(3L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(2L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotalFixedAssets(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Other Liabilities Or Assets - Test validation with total net assets error")
    void validateBalanceSheetWithOtherLiabilitiesOrAssetsTotalNetAssetsError() {
        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setNetCurrentAssets(2L);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(2L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotalFixedAssets(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }
}