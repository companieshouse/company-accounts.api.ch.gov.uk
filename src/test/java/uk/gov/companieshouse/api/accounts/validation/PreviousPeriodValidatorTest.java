package uk.gov.companieshouse.api.accounts.validation;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class PreviousPeriodValidatorTest {

    private static final String PREVIOUS_PERIOD_PATH = "$.previous_period";
    private static final String BALANCE_SHEET_PATH = PREVIOUS_PERIOD_PATH + ".balance_sheet";
    private static final String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";

    private static final long TANGIBLE = 5;
    private static final long TOTAL_FIXED_ASSETS = 10;

    PreviousPeriodValidator validator = new PreviousPeriodValidator();

    PreviousPeriod previousPeriod = new PreviousPeriod();
    BalanceSheet balanceSheet = new BalanceSheet();
    Errors errors = new Errors();

    @Test
    @DisplayName("Test total fixed assets validation")
    public void validateTotalFixedAssets() {

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(TANGIBLE);
        fixedAssets.setTotalFixedAssets(TOTAL_FIXED_ASSETS);
        balanceSheet.setFixedAssets(fixedAssets);
        previousPeriod.setBalanceSheet(balanceSheet);
        ReflectionTestUtils.setField(validator, "incorrectTotal", "incorrect_total");

        validator.validateTotalFixedAssets(previousPeriod, errors);

        assertTrue(errors.containsError(
            new Error("incorrect_total", TOTAL_PATH,
                LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType())));

    }
}