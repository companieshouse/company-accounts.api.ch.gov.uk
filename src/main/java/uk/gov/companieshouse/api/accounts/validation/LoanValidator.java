package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;

@Component
public class LoanValidator extends BaseValidator {

    private static final String LOANS_PATH = "$.loans";

    private static final String LOANS_DIRECTOR_NAME = LOANS_PATH + ".director_name";

    private static final String LOANS_DESCRIPTION = LOANS_PATH + ".description";

    private static final String LOANS_BREAKDOWN_PATH = LOANS_PATH + ".breakdown";

    private static final String LOANS_BREAKDOWN_PATH_ADVANCES_CREDIT_MADE = LOANS_PATH + ".breakdown.advances_credits_made";

    private static final String LOANS_BREAKDOWN_PATH_ADVANCES_CREDIT_REPAID = LOANS_PATH + ".breakdown.advances_credits_repaid";

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START = LOANS_PATH + ".breakdown.balance_at_period_start";

    private static final String LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END = LOANS_PATH + ".breakdown.balance_at_period_end";


	public Errors validateLoan(Loan loan) {

		Errors errors = new Errors();

		if (loan.getDirectorName() == null) {
            addError(errors, mandatoryElementMissing, LOANS_DIRECTOR_NAME);
		}

		if (loan.getDescription() == null) {
            addError(errors, mandatoryElementMissing, LOANS_DESCRIPTION);
		}

		LoanBreakdownResource loanBreakdown = loan.getBreakdown();
		
		if(loanBreakdown == null) {
            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH);
		} else {
			Boolean missingBreakdownElement = false;
			if (loanBreakdown.getAdvancesCreditsMade() == null) {
				missingBreakdownElement = true;
	            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_ADVANCES_CREDIT_MADE);
			}
	
			if (loanBreakdown.getAdvancesCreditsRepaid() == null) {
				missingBreakdownElement = true;
	            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_ADVANCES_CREDIT_REPAID);
			}
	
			if (loanBreakdown.getBalanceAtPeriodStart() == null) {
				missingBreakdownElement = true;
	            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_START);
			}
	
			if (loanBreakdown.getBalanceAtPeriodEnd() == null) {
				missingBreakdownElement = true;
	            addError(errors, mandatoryElementMissing, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
			}
			
			if(!missingBreakdownElement) {
				validateLoanCalculation(loanBreakdown, errors);
			}
		}
		
		return errors;
	}
	
	private void validateLoanCalculation(LoanBreakdownResource loanBreakdown, Errors errors) {
		if ((loanBreakdown.getBalanceAtPeriodStart() + loanBreakdown.getAdvancesCreditsMade()) - loanBreakdown.getAdvancesCreditsRepaid() != loanBreakdown.getBalanceAtPeriodEnd()) {
            addError(errors, incorrectTotal, LOANS_BREAKDOWN_PATH_BALANCE_AT_PERIOD_END);
		}
	}
}
