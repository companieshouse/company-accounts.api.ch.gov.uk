package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesValidatorTest {

    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH +
            ".previous_period";
    private static final String COMPANY_ACCOUNTS_ID = "abcd12345";

    @Mock
    CompanyService mockCompanyService;

    @Mock
    Transaction mockTransaction;

    @Mock
    private HttpServletRequest mockRequest;

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
    void testSuccessfulFirstYearNoteValidationAndCrossValidation() throws DataException {

        createValidNoteCurrentPeriod();

        errors = validator.validateEmployees(employees, mockTransaction, COMPANY_ACCOUNTS_ID, mockRequest);

        assertFalse(errors.hasErrors());
    }

    private Employees createValidNoteCurrentPeriod() {
        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setAverageNumberOfEmployees(5L);
        currentPeriod.setDetails("test");

        employees.setCurrentPeriod(currentPeriod);

        return employees;
    }

//
//    Successful multiple
//    year filer
//
//    Successful first
//    year getFiler
//
//    firstYeatFilerPrevPeriod
//

}
