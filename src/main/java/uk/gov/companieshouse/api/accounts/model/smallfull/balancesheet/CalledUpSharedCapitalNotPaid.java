package uk.gov.companieshouse.api.accounts.model.smallfull.balancesheet;

public class CalledUpSharedCapitalNotPaid {
    private int currentAmount;
    private int previousAmount;

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(int previousAmount) {
        this.previousAmount = previousAmount;
    }
}
