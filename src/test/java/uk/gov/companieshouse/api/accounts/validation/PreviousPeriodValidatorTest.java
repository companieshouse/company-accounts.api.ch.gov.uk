package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.*;
import uk.gov.companieshouse.api.accounts.model.validation.Error;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PreviousPeriodValidatorTest {

    String PREVIOUS_PERIOD_PATH = "$.previous_period";
    String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";

    PreviousPeriodValidator validator = new PreviousPeriodValidator();

    PreviousPeriod previousPeriod = new PreviousPeriod();
    BalanceSheet balanceSheet = new BalanceSheet();
    uk.gov.companieshouse.api.accounts.model.validation.Errors errors = new uk.gov.companieshouse.api.accounts.model.validation.Errors();

    @Test
    @DisplayName("Test total fixed assets validation")
    public void validateTotalFixedAssets() {

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5);
        fixedAssets.setTotalFixedAssets(10);
        balanceSheet.setFixedAssets(fixedAssets);
        previousPeriod.setBalanceSheet(balanceSheet);

        validator.validatePreviousPeriod(previousPeriod, errors);

        assertTrue(errors.containsError(
            new Error(ErrorMessageKeys.INCORRECT_TOTAL.getKey(), TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }
}