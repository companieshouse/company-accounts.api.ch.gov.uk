package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfitAndLossValidatorTest {

    private ProfitAndLoss profitAndLoss;
    private Errors errors;


    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private GrossProfitOrLoss grossProfitOrLoss;

    @Mock
    private OperatingProfitOrLoss operatingProfitOrLoss;

    @Mock
    private CompanyService companyService;

    private ProfitAndLossValidator validator;

    private static final String COMPANY_ACCOUNTS_ID = "123abcefg";
    private static final String INCORRECT_TOTAL = "incorrect_total";
    private static final String INCORRECT_TOTAL_KEY = "incorrectTotal";

    @BeforeEach
    void setup() {
        profitAndLoss = new ProfitAndLoss();
        validator = new ProfitAndLossValidator();
        errors = new Errors();
    }

    @Test
    @DisplayName("Test successful input of Gross Profit or Loss fields")
    void testCorrectGrossProfitOrLossEntry() throws DataException {
        createValidGrossProfitOrLoss();
        createValidOperatingTotal();

        errors = validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test Gross profit or loss fields do no match with total")
    void GrossProfitOfLossFieldsDoNotMatchTotal() throws DataException {

        GrossProfitOrLoss grossProfitOrLoss = new GrossProfitOrLoss();
        grossProfitOrLoss.setCostOfSales(3L);
        grossProfitOrLoss.setTurnover(5L);
        grossProfitOrLoss.setGrossTotal(0L);

        profitAndLoss.setGrossProfitOrLoss(grossProfitOrLoss);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.profit_and_loss.gross_profit_or_loss.gross_total")));
    }


    @Test
    @DisplayName("Test successful input of Gross Profit or Loss fields")
    void testOperatingTotalEntry() throws DataException {

        // grossProfitOrLoss.setGrossTotal(10L);
        createValidOperatingTotal();

        errors = validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Test Gross profit or loss fields do no match with total")
    void operatingProfitOfLossFieldsDoNotMatchTotal() throws DataException {

        createValidOperatingTotal();
        operatingProfitOrLoss.setOperatingTotal(0L);
        profitAndLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        errors = validator.validateProfitLoss(profitAndLoss, COMPANY_ACCOUNTS_ID, request, transaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.profit_and_loss.operating_profit_or_loss.operating_total")));
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

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }


}
