package uk.gov.companieshouse.api.accounts.validation.smallfull;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.validation.AccountsResourceValidator;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class EmployeesValidator extends BaseValidator implements AccountsResourceValidator<Employees> {

    private static final String EMPLOYEES_PATH = "$.employees";
    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH +
            ".previous_period";

    private CompanyService companyService;

    @Autowired
    public EmployeesValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Override
    public Errors validateSubmission(@Valid Employees employees, Transaction transaction, String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (employees.getCurrentPeriod() == null && employees.getPreviousPeriod() == null) {
            addEmptyResourceError(errors, EMPLOYEES_PATH);
        }

        if (!isMultipleYearFiler && employees.getPreviousPeriod() != null &&
                employees.getPreviousPeriod().getAverageNumberOfEmployees() != null) {
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

    @Override
    public AccountsResource getAccountsResource() {
        return AccountsResource.SMALL_FULL_EMPLOYEES;
    }
}