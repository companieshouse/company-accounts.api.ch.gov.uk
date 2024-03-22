package uk.gov.companieshouse.api.accounts.validation;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class LoanValidator extends BaseValidator {

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START = "$.loan.breakdown.balance_at_period_start";
    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = "$.loan.breakdown.balance_at_period_end";
    private static final String LOANS_DIRECTOR_NAME = "$.loan.director_name";

    private final DirectorValidator directorValidator;

    private final DirectorsReportServiceImpl directorsReportService;

    @Autowired
    public LoanValidator(CompanyService companyService,
                         DirectorValidator directorValidator,
                         DirectorsReportServiceImpl directorsReportService) {
        super(companyService);
        this.directorValidator = directorValidator;
        this.directorsReportService = directorsReportService;
    }

    public Errors validateLoan(Loan loan,
                               Transaction transaction,
                               String companyAccountId,
                               HttpServletRequest request) throws DataException {
        Errors errors = new Errors();

        boolean isMultipleYearFiler = getIsMultipleYearFiler(transaction);

        if (!isMultipleYearFiler && loan.getBreakdown().getBalanceAtPeriodStart() != null) {
            addError(errors, unexpectedData, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START);
            return errors;
        } else if (isMultipleYearFiler && loan.getBreakdown().getBalanceAtPeriodStart() == null) {
            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START);
            return errors;
        }

        validateLoanCalculation(loan.getBreakdown(), isMultipleYearFiler, errors);
        crossValidateDirectorNameDR(loan, transaction, companyAccountId, request, errors);

        return errors;
    }

    private void validateLoanCalculation(LoanBreakdownResource loanBreakdown,
                                         boolean isMultipleYearFiler,
                                         Errors errors) {
        Long advancesCreditsMade = 0L;
        Long advancesCreditsRepaid = 0L;

        if (loanBreakdown.getAdvancesCreditsMade() != null) {
            advancesCreditsMade = loanBreakdown.getAdvancesCreditsMade();
        }

        if (loanBreakdown.getAdvancesCreditsRepaid() != null) {
            advancesCreditsRepaid = loanBreakdown.getAdvancesCreditsRepaid();
        }

        if (isMultipleYearFiler) {
            if (loanBreakdown.getBalanceAtPeriodStart() + advancesCreditsMade
                    - advancesCreditsRepaid != loanBreakdown.getBalanceAtPeriodEnd()) {
                addError(errors, incorrectTotal, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
            }
        } else {
            if (advancesCreditsMade - advancesCreditsRepaid != loanBreakdown
                    .getBalanceAtPeriodEnd()) {
                addError(errors, incorrectTotal, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
            }
        }
    }

    private void crossValidateDirectorNameDR(Loan loan,
                                             Transaction transaction,
                                             String companyAccountId,
                                             HttpServletRequest request,
                                             Errors errors) throws DataException {
        if (StringUtils.isNotBlank(loan.getDirectorName())
                && (directorsReportService.find(companyAccountId, request).getStatus() == ResponseStatus.FOUND)) {
            String directorName = loan.getDirectorName();

            List<String> allNames = directorValidator.getValidDirectorNames(transaction, companyAccountId, request);

            if (!allNames.isEmpty() && !allNames.contains(directorName)) {
                addError(errors, mustMatchDirector, LOANS_DIRECTOR_NAME);
            }
        }
    }
}
