package uk.gov.companieshouse.api.accounts.model.smallfull.balancesheet;

public class BalanceSheet {

    private String currentPeriodDateFormatted;
    private String previousPeriodDateFormatted;

    private CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid;

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

    public void setCalledUpSharedCapitalNotPaid(CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid) {
        this.calledUpSharedCapitalNotPaid = calledUpSharedCapitalNotPaid;
    }
}
