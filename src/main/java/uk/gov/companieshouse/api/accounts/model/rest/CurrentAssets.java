package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class CurrentAssets {

    public static final int MAX_RANGE = 99999999;
    public static final int MIN_RANGE = 0;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("stocks")
    private Long stocks;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("debtors")
    private Long debtors;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("cash_at_bank_and_in_hand")
    private Long cashAtBankAndInHand;

    @NotNull
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
