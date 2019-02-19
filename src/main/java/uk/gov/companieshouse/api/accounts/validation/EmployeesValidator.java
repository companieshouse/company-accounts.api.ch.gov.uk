package uk.gov.companieshouse.api.accounts.validation;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public class EmployeesValidator extends BaseValidator {

    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH +
            ".previous_period";

    private CompanyService companyService;

    @Autowired
    public EmployeesValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Errors validateEmployees(@Valid Employees employees, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (! isMultipleYearFiler && employees.getPreviousPeriod() != null) {
            addError(errors, unexpectedData, EMPLOYEES_PREVIOUS_PERIOD_PATH);
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