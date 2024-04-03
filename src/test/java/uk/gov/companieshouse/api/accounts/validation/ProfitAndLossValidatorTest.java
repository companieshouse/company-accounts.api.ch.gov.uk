package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossBeforeTax;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossForFinancialYear;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProfitAndLossValidatorTest {
    private ProfitAndLoss profitAndLoss;

    private Errors errors;

    @Mock
    private GrossProfitOrLoss grossProfitOrLoss;

    @Mock
    private OperatingProfitOrLoss operatingProfitOrLoss;

    @Mock
    private ProfitOrLossBeforeTax profitOrLossBeforeTax;

    @Mock
    private ProfitOrLossForFinancialYear profitOrLossForFinancialYear;

    @Mock
    private CompanyService companyService;
    
    private ProfitAndLossValidator validator;

    private static final String INCORRECT_TOTAL = "incorrect_total";
    private static final String INCORRECT_TOTAL_KEY = "incorrectTotal";

    private static final String VALUE_REQUIRED = "value_required";
    private static final String VALUE_REQUIRED_KEY = "valueRequired";

    @BeforeEach
    void setup() {
        profitAndLoss = new ProfitAndLoss();
        validator = new ProfitAndLossValidator(companyService);
        errors = new Errors();
    }

    @Test
    @DisplayName("Test submission of empty Profit and Loss")
    void testSubmissionOfEmptyProfitAndLoss() {
        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        errors = validator.validateProfitLoss(profitAndLoss);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED,
                "$.profit_and_loss.profit_or_loss_for_financial_year.total_profit_or_loss_for_financial_year")));
    }

    @Test
    @DisplayName("Test successful input of Gross Profit or Loss fields")
    void testCorrectGrossProfitOrLossEntry() {
        createValidGrossProfitOrLoss();
        createValidOperatingTotal();
        createValidTotalProfitOrLossBeforeTax();
        createValidTotalProfitOrLossForFinancialYear();

        errors = validator.validateProfitLoss(profitAndLoss);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test Gross profit or loss fields do no match with total")
    void grossProfitOrLossFieldsDoNotMatchTotal() {
        GrossProfitOrLoss grossProfitOrLoss = new GrossProfitOrLoss();
        grossProfitOrLoss.setCostOfSales(3L);
        grossProfitOrLoss.setTurnover(5L);
        grossProfitOrLoss.setGrossTotal(0L);

        profitAndLoss.setGrossProfitOrLoss(grossProfitOrLoss);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL,
                "$.profit_and_loss.gross_profit_or_loss.gross_total")));
    }


    @Test
    @DisplayName("Test successful input of operating Profit or Loss fields")
    void testOperatingTotalEntry() {
        createValidOperatingTotal();
        createValidTotalProfitOrLossBeforeTax();
        createValidTotalProfitOrLossForFinancialYear();
        errors = validator.validateProfitLoss(profitAndLoss);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test operating profit or loss fields do no match with total")
    void operatingProfitOfLossFieldsDoNotMatchTotal() {
        createValidOperatingTotal();
        operatingProfitOrLoss.setOperatingTotal(0L);
        profitAndLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL,
                "$.profit_and_loss.operating_profit_or_loss.operating_total")));
    }

    @Test
    @DisplayName("Test successful input of profit or loss before tax fields")
    void testTotalProfitOrLossBeforeTaxEntry() {
        createValidTotalProfitOrLossBeforeTax();
        createValidTotalProfitOrLossForFinancialYear();

        errors = validator.validateProfitLoss(profitAndLoss);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test profit or loss before tax fields do no match with total")
    void profitOrLossBeforeTaxFieldsDoNotMatchTotal() {
        createValidTotalProfitOrLossBeforeTax();
        profitOrLossBeforeTax.setTotalProfitOrLossBeforeTax(0L);
        profitAndLoss.setProfitOrLossBeforeTax(profitOrLossBeforeTax);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL,
                "$.profit_and_loss.profit_or_loss_before_tax.total_profit_or_loss_before_tax")));
    }

    @Test
    @DisplayName("Test successful input of profit or loss for financial year fields")
    void testTotalProfitOrLossForFinancialYearEntry() {
        createValidTotalProfitOrLossForFinancialYear();

        errors = validator.validateProfitLoss(profitAndLoss);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test profit or loss for financial year fields do no match with total")
    void profitOrLossForFinancialYearDoNotMatchTotal() {
        createValidTotalProfitOrLossForFinancialYear();
        profitOrLossForFinancialYear.setTotalProfitOrLossForFinancialYear(0L);
        profitAndLoss.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL,
                "$.profit_and_loss.profit_or_loss_for_financial_year.total_profit_or_loss_for_financial_year")));
    }

    private void createValidOperatingTotal() {
        createValidGrossProfitOrLoss();
        operatingProfitOrLoss = new OperatingProfitOrLoss();
        operatingProfitOrLoss.setAdministrativeExpenses(2L);
        operatingProfitOrLoss.setDistributionCosts(1L);
        operatingProfitOrLoss.setOtherOperatingIncome(3L);
        operatingProfitOrLoss.setOperatingTotal(3L);

        profitAndLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);
    }

    private void createValidGrossProfitOrLoss() {
        grossProfitOrLoss = new GrossProfitOrLoss();
        grossProfitOrLoss.setTurnover(6L);
        grossProfitOrLoss.setCostOfSales(3L);
        grossProfitOrLoss.setGrossTotal(3L);

        profitAndLoss.setGrossProfitOrLoss(grossProfitOrLoss);
    }

    private void createValidTotalProfitOrLossBeforeTax() {
        createValidOperatingTotal();
        profitOrLossBeforeTax = new ProfitOrLossBeforeTax();
        profitOrLossBeforeTax.setInterestPayableAndSimilarCharges(2L);
        profitOrLossBeforeTax.setInterestReceivableAndSimilarIncome(2L);
        profitOrLossBeforeTax.setTotalProfitOrLossBeforeTax(3L);

        profitAndLoss.setProfitOrLossBeforeTax(profitOrLossBeforeTax);
    }

    private void  createValidTotalProfitOrLossForFinancialYear() {
        createValidTotalProfitOrLossBeforeTax();
        profitOrLossForFinancialYear = new ProfitOrLossForFinancialYear();
        profitOrLossForFinancialYear.setTax(1L);
        profitOrLossForFinancialYear.setTotalProfitOrLossForFinancialYear(2L);

        profitAndLoss.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);
    }
    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
