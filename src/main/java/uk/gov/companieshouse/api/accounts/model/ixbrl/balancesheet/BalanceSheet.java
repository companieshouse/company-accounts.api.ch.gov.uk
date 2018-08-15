package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;

public class BalanceSheet {
    
    private uk.gov.companieshouse.accountsDates.impl.AccountsDatesImpl dateHelper;
    private Period period;

    @JsonProperty("current_period_date_formatted")
    private String currentPeriodDateFormatted;
    @JsonProperty("previous_period_date_formatted")
    private String previousPeriodDateFormatted;
    @JsonProperty("called_up_shared_capital_not_paid")
    private CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid;
    @JsonProperty("balance_sheet_statements")
    private BalanceSheetStatements balanceSheetStatements;
    

    public BalanceSheetStatements getBalanceSheetStatements() {
        return balanceSheetStatements;
    }

    public void setBalanceSheetStatements(BalanceSheetStatements balanceSheetStatements) {
        this.balanceSheetStatements = balanceSheetStatements;
    }

    public String getCurrentPeriodDateFormatted() {
        return currentPeriodDateFormatted;
    }

    public void setCurrentPeriodDateFormatted(String currentPeriodDateFormatted) {
        this.currentPeriodDateFormatted = currentPeriodDateFormatted;
    }

    public String getPreviousPeriodDateFormatted() {
        return previousPeriodDateFormatted;
    }

    public void setPreviousPeriodDateFormatted(String previousPeriodDateFormatted) {
        this.previousPeriodDateFormatted = previousPeriodDateFormatted;
    }

    public CalledUpSharedCapitalNotPaid getCalledUpSharedCapitalNotPaid() {
        return calledUpSharedCapitalNotPaid;
    }

    public void setCalledUpSharedCapitalNotPaid(
        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid) {
        this.calledUpSharedCapitalNotPaid = calledUpSharedCapitalNotPaid;
    }
}
