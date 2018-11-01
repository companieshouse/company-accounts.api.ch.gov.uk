package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class CurrentAssetsEntity {

    @Field("total_current_assets")
    private Long totalCurrentAssets;

    @Field("stocks")
    private Long stocks;

    @Field("debtors")
    private Long debtors;

    @Field("cash_at_bank_and_in_hand")
    private Long cashAtBankAndInHand;

    public Long getTotalCurrentAssets() {
        return totalCurrentAssets;
    }

    public void setTotalCurrentAssets(Long totalCurrentAssets) {
        this.totalCurrentAssets = totalCurrentAssets;
    }

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
}
