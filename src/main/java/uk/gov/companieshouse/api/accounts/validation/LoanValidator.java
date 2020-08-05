package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

@Component
public class LoanValidator extends BaseValidator {

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = "$.loan.breakdown.balance_at_period_end";

	public Errors validateLoan(Loan loan) {

		Errors errors = new Errors();

		validateLoanCalculation(loan.getBreakdown(), errors);
		
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
}
