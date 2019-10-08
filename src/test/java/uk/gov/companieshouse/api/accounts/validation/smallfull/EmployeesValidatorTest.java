package uk.gov.companieshouse.api.accounts.validation.smallfull;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesValidatorTest {

    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH + ".previous_period";

    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";

    private static final String EMPTY_RESOURCE_NAME = "emptyResource";
    private static final String EMPTY_RESOURCE_VALUE = "empty_resource";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Mock
    private CompanyService companyService;

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;
    
    @InjectMocks
    private EmployeesValidator validator;

    @Test
    @DisplayName("Note validation with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException, ServiceException {

        Employees employees = createNote(true, false);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        Errors errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation when empty note submitted")
    void testEmptyResourceValidation() throws DataException, ServiceException {

        Employees employees = createNote(false, false);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, EMPTY_RESOURCE_NAME,
                EMPTY_RESOURCE_VALUE);

        Errors errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertTrue(errors.containsError(createError(EMPTY_RESOURCE_VALUE,
                EMPLOYEES_PATH)));
    }

    @Test
    @DisplayName("Note validation with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws DataException, ServiceException {

        Employees employees = createNote(true, true);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(true);

        Errors errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error returned for first year filer if previous period provided in note")
    void testUnexpectedDataErrorReturnedForFirstYearFiler() throws ServiceException, DataException {

        Employees employees = createNote(true, true);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        when(companyService.isMultipleYearFiler(transaction)).thenReturn(false);

        Errors errors = validator.validateSubmission(employees, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                EMPLOYEES_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {

        when(companyService.isMultipleYearFiler(transaction)).thenThrow(ServiceException.class);

        assertThrows(DataException.class,
                () -> validator.validateSubmission(new Employees(), transaction, COMPANY_ACCOUNTS_ID, request));
    }

    private Employees createNote(boolean includeCurrentPeriod, boolean includePreviousPeriod) {

        Employees employees = new Employees();

        if (includeCurrentPeriod) {

            CurrentPeriod currentPeriod = new CurrentPeriod();
            currentPeriod.setAverageNumberOfEmployees(5L);
            currentPeriod.setDetails("test");

            employees.setCurrentPeriod(currentPeriod);
        }

        if (includePreviousPeriod) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            previousPeriod.setAverageNumberOfEmployees(10L);

            employees.setPreviousPeriod(previousPeriod);
        }

        return employees;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
