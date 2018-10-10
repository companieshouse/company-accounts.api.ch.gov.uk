package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrentPeriodValidatorTest {

    private static final String CURRENT_PERIOD_PATH = "$.current_period";
    private static final String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    private static final String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";

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



       Errors errors =  validator.validateCurrentPeriod(currentPeriod);

        assertTrue(errors.containsError(
            new Error(ErrorMessageKeys.INCORRECT_TOTAL.getKey(), TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }
}