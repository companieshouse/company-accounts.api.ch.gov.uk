package uk.gov.companieshouse.api.accounts.validation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PreviousPeriodValidatorTest {

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";
    private static final String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    private static final String FIXED_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
    private static final String OTHER_LIABILITIES_OR_ASSETS_PATH = BALANCE_SHEET_PATH + ".other_liabilities_or_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".net_current_assets";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_assets_less_current_liabilities";
    private static final String OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH = OTHER_LIABILITIES_OR_ASSETS_PATH + ".total_net_assets";
    private static final String CURRENT_ASSETS_TOTAL_PATH = BALANCE_SHEET_PATH + ".current_assets.total";

    private BalanceSheet balanceSheet;
    private PreviousPeriod previousPeriod;
    private Errors errors;

    PreviousPeriodValidator validator = new PreviousPeriodValidator();

    @BeforeEach
    public void setup() {

        previousPeriod = new PreviousPeriod();
        balanceSheet = new BalanceSheet();
        errors = new Errors();

    }

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
        fixedAssets.setTotal(2L);
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
        fixedAssets.setTotal(10L);
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

        addInvalidFixedAssetsToBalanceSheet();
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
        fixedAssets.setTotal(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        Errors errors =  validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("incorrect_total", OTHER_LIABILITIES_OR_ASSETS_TOTAL_NET_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("ERROR - Mandatory elements missing for NetCurrentAssets and TotalAssetsLessCurrentLiabilities")
    void validateMissingField() {
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setTotal(3L);
        balanceSheet.setCurrentAssets(currentAssets);

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(4L);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(2L);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(1L);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(1L);
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(1L);
        otherLiabilitiesOrAssets.setTotalNetAssets(1L);
        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(2L);
        fixedAssets.setTotal(2L);
        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");
        ReflectionTestUtils.setField(validator, "mandatoryElementMissing", "mandatory_element_missing");

        Errors errors = validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.containsError(
                new Error("mandatory_element_missing", OTHER_LIABILITIES_OR_ASSETS_NET_CURRENT_ASSETS_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));
    }

    @Test
    @DisplayName("Test incorrect total current assets validation")
    public void validateTotalCurrentAssets() {

        addInvalidCurrentAssetsToBalanceSheet();
        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(previousPeriod, errors);

        assertTrue(errors.containsError(
                new Error("incorrect_total", CURRENT_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Test current assets validation with empty values")
    public void validateTotalCurrentAssetsWithEmptyValues() {

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(null);
        currentAssets.setDebtors(null);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setTotal(10L);

        balanceSheet.setCurrentAssets(currentAssets);
        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(previousPeriod, errors);

        assertTrue(errors.containsError(
                new Error("incorrect_total", CURRENT_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Test total current assets no error with correct values")
    public void validateTotalCurrentAssetsWithCorrectValues() {

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(5L);
        currentAssets.setDebtors(5L);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setTotal(15L);

        balanceSheet.setCurrentAssets(currentAssets);

        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(previousPeriod, errors);

        assertFalse(errors.hasErrors());

    }

    @Test
    @DisplayName("Test validate whole current period")
    public void validateCurrentPeriod(){

        addInvalidFixedAssetsToBalanceSheet();
        addInvalidCurrentAssetsToBalanceSheet();

        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        errors = validator.validatePreviousPeriod(previousPeriod);

        assertTrue(errors.hasErrors());
        assertEquals(2, errors.getErrorCount());
    }

    private void addInvalidFixedAssetsToBalanceSheet() {
        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5L);
        fixedAssets.setTotal(10L);

        balanceSheet.setFixedAssets(fixedAssets);
    }

    private void addInvalidCurrentAssetsToBalanceSheet() {
        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(5L);
        currentAssets.setDebtors(5L);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setTotal(10L);

        balanceSheet.setCurrentAssets(currentAssets);
    }
}