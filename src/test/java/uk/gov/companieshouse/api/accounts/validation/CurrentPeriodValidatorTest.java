package uk.gov.companieshouse.api.accounts.validation;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrentPeriodValidatorTest {

    private static final String CURRENT_PERIOD_PATH = "$.current_period";
    private static final String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    private static final String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";

    private static String CURRENT_ASSETS_TOTAL_PATH = "$.current_period.balance_sheet.current_assets.total";

    CurrentPeriodValidator validator = new CurrentPeriodValidator();

    CurrentPeriod currentPeriod = new CurrentPeriod();
    BalanceSheet balanceSheet = new BalanceSheet();
    Errors errors = new Errors();

    @Test
    @DisplayName("Test total fixed assets validation")
    public void validateTotalFixedAssets() {

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5L);
        fixedAssets.setTotalFixedAssets(10L);
        balanceSheet.setFixedAssets(fixedAssets);
        currentPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalFixedAssets(currentPeriod, errors);

        assertTrue(errors.containsError(
            new Error("incorrect_total", TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }

    @Test
    @DisplayName("Test total current assets validation")
    public void validateTotalCurrentAssets(){

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(5L);
        currentAssets.setDebtors(5L);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setTotalCurrentAssets(10L);

        balanceSheet.setCurrentAssets(currentAssets);
        currentPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(currentPeriod, errors);

        assertTrue(errors.containsError(
                new Error("incorrect_total", CURRENT_ASSETS_TOTAL_PATH,
                        LocationType.JSON_PATH.getValue(),
                        ErrorType.VALIDATION.getType())));

    }


    @Test
    @DisplayName("Test total current assets validation with empty values")
    public void validateTotalCurrentAssetsWithEmptyValues() {

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setStocks(null);
        currentAssets.setDebtors(null);
        currentAssets.setCashAtBankAndInHand(5L);
        currentAssets.setTotalCurrentAssets(10L);

        balanceSheet.setCurrentAssets(currentAssets);
        currentPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(currentPeriod, errors);

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
        currentAssets.setTotalCurrentAssets(15L);

        balanceSheet.setCurrentAssets(currentAssets);
        currentPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalCurrentAssets(currentPeriod, errors);

        assertFalse(errors.hasErrors());

}
}