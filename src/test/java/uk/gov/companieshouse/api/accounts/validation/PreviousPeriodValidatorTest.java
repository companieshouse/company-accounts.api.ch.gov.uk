package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
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
    private static final String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";
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
    @DisplayName("Test total fixed assets validation")
    public void validateTotalFixedAssets() {

        addInvalidFixedAssetsToBalanceSheet();
        previousPeriod.setBalanceSheet(balanceSheet);

        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalFixedAssets(previousPeriod, errors);

        assertTrue(errors.containsError(
                new Error("incorrect_total", TOTAL_PATH,
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
        fixedAssets.setTotalFixedAssets(10L);

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