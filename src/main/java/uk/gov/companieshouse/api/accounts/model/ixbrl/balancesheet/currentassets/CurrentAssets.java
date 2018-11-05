package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.currentassets;

public class CurrentAssets {

    private Stocks stocks;
    private Debtors debtors;
    private CashAtBankInHand cashAtBankInHand;

    private Long currentTotalCurrentAssets;
    private Long previousTotalCurrentAssets;

    public Stocks getStocks() {
        return stocks;
    }

    public void setStocks(Stocks stocks) {
        this.stocks = stocks;
    }

    public Debtors getDebtors() {
        return debtors;
    }

    public void setDebtors(Debtors debtors) {
        this.debtors = debtors;
    }

    public CashAtBankInHand getCashAtBankInHand() {
        return cashAtBankInHand;
    }

    public void setCashAtBankInHand(CashAtBankInHand cashAtBankInHand) {
        this.cashAtBankInHand = cashAtBankInHand;
    }

    public Long getCurrentTotalCurrentAssets() {
        return currentTotalCurrentAssets;
    }

    public void setCurrentTotalCurrentAssets(Long currentTotalCurrentAssets) {
        this.currentTotalCurrentAssets = currentTotalCurrentAssets;
    }

    public Long getPreviousTotalCurrentAssets() {
        return previousTotalCurrentAssets;
    }

    public void setPreviousTotalCurrentAssets(Long previousTotalCurrentAssets) {
        this.previousTotalCurrentAssets = previousTotalCurrentAssets;
    }
}
