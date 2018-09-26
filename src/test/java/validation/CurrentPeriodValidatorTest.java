package validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.CurrentPeriodValidator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrentPeriodValidatorTest {

    String CURRENT_PERIOD_PATH = "$.current_period";
    String BALANCE_SHEET_PATH = CURRENT_PERIOD_PATH + ".balance_sheet";
    String TOTAL_PATH = BALANCE_SHEET_PATH + ".fixed_assets.total";

    CurrentPeriodValidator validator = new CurrentPeriodValidator();

    CurrentPeriod currentPeriod = new CurrentPeriod();
    BalanceSheet balanceSheet = new BalanceSheet();
    Errors errors = new Errors();

    @Test
    @DisplayName("Test total fixed assets validation")
    public void validateTotalFixedAssets() {
        
        Errors errors = new Errors();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(5);
        fixedAssets.setTotalFixedAssets(10);
        balanceSheet.setFixedAssets(fixedAssets);
        currentPeriod.setBalanceSheet(balanceSheet);

        validator.validateCurrentPeriod(currentPeriod, errors);

        System.out.println(errors);
   
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
    }
}
