package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class LoanValidator extends BaseValidator {

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = "$.loan.breakdown.balance_at_period_end";
	private static final String LOANS_DIRECTOR_NAME = "$.loan.director_name";

	@Autowired
	private DirectorService directorService;

	@Autowired
	private DirectorValidator directorValidator;

	@Autowired
	private DirectorsReportServiceImpl directorsReportService;

	public Errors validateLoan(Loan loan, Transaction transaction,
							   String companyAccountId, HttpServletRequest request) throws DataException {

		Errors errors = new Errors();

		validateLoanCalculation(loan.getBreakdown(), errors);
		crossValidateDirectorNameDR(loan, transaction, companyAccountId, request, errors);
		
		return errors;
	}
	
	private void validateLoanCalculation(LoanBreakdownResource loanBreakdown, Errors errors) {

		Long advancesCreditsMade = 0L;
		Long advancesCreditsRepaid = 0L;
		
		if (loanBreakdown.getAdvancesCreditsMade() != null) {
			advancesCreditsMade = loanBreakdown.getAdvancesCreditsMade();
		}
		
		if (loanBreakdown.getAdvancesCreditsRepaid() != null) {
			advancesCreditsRepaid = loanBreakdown.getAdvancesCreditsRepaid();
		}
		
		if (loanBreakdown.getBalanceAtPeriodStart() + advancesCreditsMade - advancesCreditsRepaid != loanBreakdown.getBalanceAtPeriodEnd()) {
            addError(errors, incorrectTotal, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
		}
	}

	private void crossValidateDirectorNameDR(Loan loan, Transaction transaction, String companyAccountId, HttpServletRequest request, Errors errors)
			throws DataException {

		if (directorsReportService.find(companyAccountId, request).getStatus() == ResponseStatus.FOUND) {
			String directorName = loan.getDirectorName();

			List<String> allNames = directorValidator.getValidDirectorNames(transaction, companyAccountId, request);

			if (!allNames.isEmpty() && !allNames.contains(directorName)) {

				addError(errors, mustMatchDirector, LOANS_DIRECTOR_NAME);
			}
		}
	}
}
