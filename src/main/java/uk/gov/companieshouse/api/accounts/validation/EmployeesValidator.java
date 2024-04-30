package uk.gov.companieshouse.api.accounts.validation;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class EmployeesValidator extends BaseValidator implements NoteValidator<Employees> {

	private static final String EMPLOYEES_PATH = "$.employees";

    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH = EMPLOYEES_PATH + ".previous_period";

    private static final String EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH + ".previous_period.average_number_of_employees";

    private static final String EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES = EMPLOYEES_PATH +  ".current_period.average_number_of_employees";

    @Autowired
    public EmployeesValidator(CompanyService companyService) {
        super(companyService);
    }

    @Override
    public Errors validateSubmission(Employees employees,
                                     Transaction transaction,
                                     String companyAccountId,
                                     HttpServletRequest request) throws DataException {
        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (isMultipleYearFiler) {
            if (employees.getPreviousPeriod() == null
                    || employees.getPreviousPeriod().getAverageNumberOfEmployees() == null) {
                addError(errors, mandatoryElementMissing, EMPLOYEES_PREVIOUS_PERIOD_PATH_AVERAGE_EMPLOYEES);
            }

        } else {
            if (employees.getPreviousPeriod() != null) {
                addError(errors, unexpectedData, EMPLOYEES_PREVIOUS_PERIOD_PATH);
            }
        }

        if (employees.getCurrentPeriod() == null
                || employees.getCurrentPeriod().getAverageNumberOfEmployees() == null) {
            addError(errors, mandatoryElementMissing, EMPLOYEES_CURRENT_PERIOD_PATH_AVERAGE_EMPLOYEES);
        }

        return errors;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_EMPLOYEES;
    }
}