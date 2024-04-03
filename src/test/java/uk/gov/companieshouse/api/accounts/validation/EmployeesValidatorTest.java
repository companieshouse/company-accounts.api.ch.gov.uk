package uk.gov.companieshouse.api.accounts.validation;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeesValidatorTest {
    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH + ".previous_period";
    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";

    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE = "mandatory.element.missing";

    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH + ".previous_period.average_number_of_employees";
    private static final String EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH +  ".current_period.average_number_of_employees";
    
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Mock
    private CompanyService companyService;

    @Mock
    private Transaction transaction;

    @Mock
    private ServiceException serviceException;
    
    @Mock
    private HttpServletRequest request;

    private Employees employees;
    private Errors errors;
    private EmployeesValidator validator;

    @BeforeEach
    void setup() {
        employees = new Employees();
        errors = new Errors();
        validator = new EmployeesValidator(companyService);
    }

    @Test
    @DisplayName("Note validation with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException, ServiceException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation multi year filer when no current and previous periods or average employees provided")
    void testEmptyResourceValidationMultiYearFiler() throws DataException, ServiceException {
        Employees employees = new Employees();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES)));

        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES)));

        assertEquals(2, errors.getErrorCount());
    }

    @Test
    @DisplayName("Note validation single year filer when previous period provided")
    void testEmptyResourceValidationSingleYearFiler() throws DataException, ServiceException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME, UNEXPECTED_DATA_VALUE);

        errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE, EMPLOYEES_PREVIOUS_PERIOD_PATH)));

        assertEquals(1, errors.getErrorCount());
    }

    @Test
    @DisplayName("Note validation with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws DataException, ServiceException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error returned for first year filer if previous period provided in note")
    void testUnexpectedDataErrorReturnedForFirstYearFiler() throws ServiceException, DataException {
        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME, UNEXPECTED_DATA_VALUE);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE, EMPLOYEES_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {
        when(companyService.isMultipleYearFiler(transaction)).thenThrow(serviceException);

        assertThrows(DataException.class,
                () -> validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request));
    }
    
    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {
        assertEquals(AccountingNoteType.SMALL_FULL_EMPLOYEES, validator.getAccountingNoteType());
    }

    private void createValidNoteCurrentPeriod() {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setAverageNumberOfEmployees(5L);
        currentPeriod.setDetails("test");

        employees.setCurrentPeriod(currentPeriod);
    }

    private void createValidNotePreviousPeriod() {
        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setAverageNumberOfEmployees(10L);

        employees.setPreviousPeriod(previousPeriod);
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
