package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanValidatorTest {

    private static final String LOANS_PATH = "$.loans";
    private static final String LOANS_DIRECTOR_NAME_PATH = LOANS_PATH + ".director_name";
    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = LOANS_PATH +  ".breakdown.balance_at_period_end";

    private static final String LOAN_DIRECTOR_NAME = "directorName";
    private static final String LOAN_DESCRIPTION = "description";

    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE = "mandatory.element.missing";

    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect.total";

    @Mock
    private CompanyService companyService;

    @Mock
    private Transaction transaction;

    @Mock
    private ServiceException serviceException;
    
    @Mock
    private HttpServletRequest request;

    private Loan loan;
    private Errors errors;
    private LoanValidator validator;

    @BeforeEach
    void setup() {
        loan = new Loan();
        errors = new Errors();
        validator = new LoanValidator();
    }

    @Test
    @DisplayName("Loan validation with valid loan and breakdown")
    void testSuccessfulLoanCalculationValidation() {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidLoanBreakdown();

        errors = validator.validateLoan(loan);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing director's name")
    void testMissingLoanDirectorsName() {
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidLoanBreakdown();

        errors = validator.validateLoan(loan);

        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
        		LOANS_DIRECTOR_NAME_PATH)));

        assertEquals(1, errors.getErrorCount());
    }

    @Test
    @DisplayName("Loan validation with missing balance_at_period_start")
    void testSuccessfulLoanCalculationValidationWithMissingBalanceAtPeriodStart() {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(null);
        loanBreakdown.setAdvancesCreditsMade(2000L);
        loanBreakdown.setAdvancesCreditsRepaid(1000L);
        loanBreakdown.setBalanceAtPeriodEnd(1000L);

        loan.setBreakdown(loanBreakdown);

        loan.getBreakdown().setBalanceAtPeriodStart(null);
        
        errors = validator.validateLoan(loan);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing advances_credits_made")
    void testSuccessfulLoanCalculationValidationWithMissingAdvancesCreditsMade() {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(2000L);
        loanBreakdown.setAdvancesCreditsMade(null);
        loanBreakdown.setAdvancesCreditsRepaid(1000L);
        loanBreakdown.setBalanceAtPeriodEnd(1000L);

        loan.setBreakdown(loanBreakdown);

        errors = validator.validateLoan(loan);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing advances_credits_repaid")
    void testSuccessfulLoanCalculationValidationWithMissingAdvancesCreditsRepaid() {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(2000L);
        loanBreakdown.setAdvancesCreditsMade(1000L);
        loanBreakdown.setAdvancesCreditsRepaid(null);
        loanBreakdown.setBalanceAtPeriodEnd(3000L);

        loan.setBreakdown(loanBreakdown);
        
        errors = validator.validateLoan(loan);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing balance_at_period_end")
    void testMissingLoanBreakdownBalanceAtPeriodEnd() {
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidLoanBreakdown();

        loan.getBreakdown().setBalanceAtPeriodEnd(null);
        
        errors = validator.validateLoan(loan);

        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
        		LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END)));

        assertEquals(1, errors.getErrorCount());
    }

    @Test
    @DisplayName("Loan validation with missing director's name and incorrect loan calculation")
    void testMissingLoanDirectorNameAndIncorrectLoanCalculation() {
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);
        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME,
                INCORRECT_TOTAL_VALUE);

    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidLoanBreakdown();

        loan.getBreakdown().setBalanceAtPeriodEnd(3000L);
        
        errors = validator.validateLoan(loan);

        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
        		LOANS_DIRECTOR_NAME_PATH)));

        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
        		LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END)));

        assertEquals(2, errors.getErrorCount());
    }

    private Loan createValidLoanBreakdown() {
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(1000L);
        loanBreakdown.setAdvancesCreditsMade(3000L);
        loanBreakdown.setAdvancesCreditsRepaid(2000L);
        loanBreakdown.setBalanceAtPeriodEnd(2000L);

        loan.setBreakdown(loanBreakdown);

        return loan;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
