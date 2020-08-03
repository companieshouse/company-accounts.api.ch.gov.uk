package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

@Component
public class LoanValidator extends BaseValidator {

    private static final String LOANS_PATH = "$.loans";

    private static final String LOANS_DIRECTOR_NAME = LOANS_PATH + ".director_name";

    private static final String LOANS_BREAKDOWN_PATH = LOANS_PATH + ".breakdown";

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = LOANS_PATH + ".breakdown.balance_at_period_end";


	public Errors validateLoan(Loan loan) {

		Errors errors = new Errors();

		if (loan.getDirectorName() == null) {
            addError(errors, mandatoryElementMissing, LOANS_DIRECTOR_NAME);
		}

		LoanBreakdownResource loanBreakdown = loan.getBreakdown();
		
		if(loanBreakdown == null) {
            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH);
		} else {
			if (loanBreakdown.getBalanceAtPeriodEnd() == null) {
				addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
			} else {
				validateLoanCalculation(loanBreakdown, errors);
			}
		}
		
		return errors;
	}
	
	private void validateLoanCalculation(LoanBreakdownResource loanBreakdown, Errors errors) {
		Long balanceAtPeriodStart = 0L;
		Long advancesCreditsMade = 0L;
		Long advancesCreditsRepaid = 0L;
		
		if (loanBreakdown.getBalanceAtPeriodStart() != null) {
			balanceAtPeriodStart = loanBreakdown.getBalanceAtPeriodStart();
		}
		
		if (loanBreakdown.getAdvancesCreditsMade() != null) {
			advancesCreditsMade = loanBreakdown.getAdvancesCreditsMade();
		}
		
		if (loanBreakdown.getAdvancesCreditsRepaid() != null) {
			advancesCreditsRepaid = loanBreakdown.getAdvancesCreditsRepaid();
		}
		
		if ((balanceAtPeriodStart + advancesCreditsMade) - advancesCreditsRepaid != loanBreakdown.getBalanceAtPeriodEnd()) {
            addError(errors, incorrectTotal, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
		}
	}
}
