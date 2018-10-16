package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentAssets {

    @JsonProperty("stocks")
    private Long stocks;
    @JsonProperty("debtors")
    private Long debtors;
    @JsonProperty("cash_at_bank_and_in_hand")
    private Long cashAtBankAndInHand;
    @JsonProperty("total")
    private Long totalCurrentAssets;

    public Long getStocks() {
        return stocks;
    }

    public void setStocks(Long stocks) {
        this.stocks = stocks;
    }

    public Long getDebtors() {
        return debtors;
    }

    public void setDebtors(Long debtors) {
        this.debtors = debtors;
    }

    public Long getCashAtBankAndInHand() {
        return cashAtBankAndInHand;
    }

    public void setCashAtBankAndInHand(Long cashAtBankAndInHand) {
        this.cashAtBankAndInHand = cashAtBankAndInHand;
    }

    public Long getTotalCurrentAssets() {
        return totalCurrentAssets;
    }

    public void setTotalCurrentAssets(Long totalCurrentAssets) {
        this.totalCurrentAssets = totalCurrentAssets;
    }
}
