package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesValidatorTest {

    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH +
            ".previous_period";
    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";

    private static final String EMPTY_RESOURCE_NAME = "emptyResource";
    private static final String EMPTY_RESOURCE_VALUE =
            "empty_resource";

    @Mock
    CompanyService mockCompanyService;

    @Mock
    Transaction mockTransaction;

    @Mock
    private ServiceException mockServiceException;

    private Employees employees;
    private Errors errors;
    private EmployeesValidator validator;

    @BeforeEach
    void setup() {
        employees = new Employees();
        errors = new Errors();
        validator = new EmployeesValidator(mockCompanyService);
    }

    @Test
    @DisplayName("Note validation with valid note for first year filer")
    void testSuccessfulFirstYearNoteValidation() throws DataException {

        createValidNoteCurrentPeriod();

        errors = validator.validateEmployees(employees, mockTransaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note validation when empty note submitted")
    void testEmptyResourceValidation() throws DataException, ServiceException {
        Employees employees = new Employees();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        ReflectionTestUtils.setField(validator, EMPTY_RESOURCE_NAME,
                EMPTY_RESOURCE_VALUE);

        errors = validator.validateEmployees(employees, mockTransaction);

        assertTrue(errors.containsError(createError(EMPTY_RESOURCE_VALUE,
                EMPLOYEES_PATH)));
    }

    @Test
    @DisplayName("Note validation with valid note for multiple year filer")
    void testSuccessfulMultipleYearNoteValidation() throws DataException, ServiceException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(true);

        errors = validator.validateEmployees(employees, mockTransaction);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error returned for first year filer if previous period provided in note")
    void testUnexpectedDataErrorReturnedForFirstYearFiler() throws ServiceException, DataException {

        createValidNoteCurrentPeriod();
        createValidNotePreviousPeriod();

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenReturn(false);

        errors = validator.validateEmployees(employees, mockTransaction);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                EMPLOYEES_PREVIOUS_PERIOD_PATH)));
    }

    @Test
    @DisplayName("Data exception thrown when company service API call fails")
    void testDataExceptionThrown() throws ServiceException {

        when(mockCompanyService.isMultipleYearFiler(mockTransaction)).thenThrow(mockServiceException);

        assertThrows(DataException.class,
                () -> validator.validateEmployees(employees, mockTransaction));
    }

    private Employees createValidNoteCurrentPeriod() {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setAverageNumberOfEmployees(5L);
        currentPeriod.setDetails("test");

        employees.setCurrentPeriod(currentPeriod);

        return employees;
    }

    private Employees createValidNotePreviousPeriod() {
        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setAverageNumberOfEmployees(10L);

        employees.setPreviousPeriod(previousPeriod);

        return employees;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
