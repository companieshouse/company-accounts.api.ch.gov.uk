package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.validation.Valid;

@Component
public class EmployeesValidator extends BaseValidator {

    private static final String EMPLOYEES_PATH = "$.employees";

    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH + ".previous_period";

    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH + ".previous_period.average_number_of_employees";

    private static final String EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH +  ".current_period.average_number_of_employees";

    private CompanyService companyService;

    @Autowired
    public EmployeesValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Errors validateEmployees(@Valid Employees employees, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (isMultipleYearFiler) {

            if (employees.getPreviousPeriod() == null || employees.getPreviousPeriod().getAverageNumberOfEmployees() == null) {

                addError(errors, mandatoryElementMissing, EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES);

            }

        } else {

            if (employees.getPreviousPeriod() != null) {

                addError(errors, unexpectedData, EMPLOYEES_PREVIOUS_PERIOD_PATH);

            }
        }

        if (employees.getCurrentPeriod() == null || employees.getCurrentPeriod().getAverageNumberOfEmployees() == null) {

            addError(errors, mandatoryElementMissing, EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES);

        }

        return errors;
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }
}