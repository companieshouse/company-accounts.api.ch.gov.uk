package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanValidatorTest {

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = "$.loan.breakdown.balance_at_period_end";

    private static final String OTHER_NAME = "otherName";

    private static final String LOAN_DIRECTOR_NAME = "directorName";
    private static final String LOAN_DESCRIPTION = "description";

    private static final String INCORRECT_TOTAL_NAME = "incorrectTotal";
    private static final String INCORRECT_TOTAL_VALUE = "incorrect.total";

    private static final String INVALID_DR_NAME = "mustMatchDirector";
    private static final String INVALID_DR_VALUE = "mustMatch.director";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Errors errors;

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private DirectorValidator directorValidator;

    @Mock
    private CompanyService companyService;
    
    private LoanValidator validator;

    private Loan loan;

    @BeforeEach
    void setup() {
        loan = new Loan();
        validator = new LoanValidator(companyService, directorValidator, directorsReportService);
    }

    @Test
    @DisplayName("Loan validation with valid loan and breakdown for multi year filer, with balance at period start")
    void testSuccessfulLoanCalculationValidationForMultiYearFiler() throws DataException, ServiceException {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidMultiYearFilerLoanBreakdown();

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(false));
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with valid loan and breakdown for single year filer, no balance at period start")
    void testSuccessfulLoanCalculationValidationForSingleYearFiler() throws DataException, ServiceException {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);
    	
        createValidSingleYearFilerLoanBreakdown();

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(false));
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing advances_credits_made")
    void testSuccessfulLoanCalculationValidationWithMissingAdvancesCreditsMade() throws DataException, ServiceException {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);

        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(2000L);
        loanBreakdown.setAdvancesCreditsMade(null);
        loanBreakdown.setAdvancesCreditsRepaid(1000L);
        loanBreakdown.setBalanceAtPeriodEnd(1000L);

        loan.setBreakdown(loanBreakdown);

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(false));
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with missing advances_credits_repaid")
    void testSuccessfulLoanCalculationValidationWithMissingAdvancesCreditsRepaid() throws DataException, ServiceException {

    	loan.setDirectorName(LOAN_DIRECTOR_NAME);
    	loan.setDescription(LOAN_DESCRIPTION);

        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(2000L);
        loanBreakdown.setAdvancesCreditsMade(1000L);
        loanBreakdown.setAdvancesCreditsRepaid(null);
        loanBreakdown.setBalanceAtPeriodEnd(3000L);

        loan.setBreakdown(loanBreakdown);

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(false));
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Loan validation with incorrect loan calculation")
    void testIncorrectLoanCalculation() throws DataException, ServiceException {

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_NAME,
                INCORRECT_TOTAL_VALUE);

    	loan.setDescription(LOAN_DESCRIPTION);

        createValidMultiYearFilerLoanBreakdown();

        loan.getBreakdown().setBalanceAtPeriodEnd(3000L);

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(false));
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(errors.containsError(createError(INCORRECT_TOTAL_VALUE,
        		LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END)));

        assertEquals(1, errors.getErrorCount());
    }

    @Test
    @DisplayName("Create valid loan with DR cross validation")
    void validLoanWithCrossValidationDR() throws DataException, ServiceException {

        List<String> validNames = new ArrayList<>();
        validNames.add(LOAN_DIRECTOR_NAME);

        loan.setDirectorName(LOAN_DIRECTOR_NAME);
        loan.setDescription(LOAN_DESCRIPTION);
        createValidMultiYearFilerLoanBreakdown();

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(true));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Create invalid loan with DR cross validation")
    void invalidLoanWithCrossValidationDR() throws DataException, ServiceException {

        ReflectionTestUtils.setField(validator, INVALID_DR_NAME,
                INVALID_DR_VALUE);

        List<String> validNames = new ArrayList<>();
        validNames.add(OTHER_NAME);

        loan.setDirectorName(LOAN_DIRECTOR_NAME);
        loan.setDescription(LOAN_DESCRIPTION);
        createValidMultiYearFilerLoanBreakdown();

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(getDirectorsReport(true));
        when(directorValidator.getValidDirectorNames(transaction, COMPANY_ACCOUNTS_ID, request)).thenReturn(validNames);
        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateLoan(loan, transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(errors.hasErrors());
    }

    private void createValidMultiYearFilerLoanBreakdown() {
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setBalanceAtPeriodStart(1000L);
        loanBreakdown.setAdvancesCreditsMade(3000L);
        loanBreakdown.setAdvancesCreditsRepaid(2000L);
        loanBreakdown.setBalanceAtPeriodEnd(2000L);

        loan.setBreakdown(loanBreakdown);
    }

    private void createValidSingleYearFilerLoanBreakdown() {
        LoanBreakdownResource loanBreakdown = new LoanBreakdownResource();
        loanBreakdown.setAdvancesCreditsMade(3000L);
        loanBreakdown.setAdvancesCreditsRepaid(2000L);
        loanBreakdown.setBalanceAtPeriodEnd(1000L);

        loan.setBreakdown(loanBreakdown);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private ResponseObject<DirectorsReport> getDirectorsReport(boolean returnFound) {

        if(returnFound) {
            return new ResponseObject<>(ResponseStatus.FOUND, directorsReport);
        } else {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
    }
}
