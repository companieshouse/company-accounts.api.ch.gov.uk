package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;
import jakarta.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class CurrentAssets {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("stocks")
    private Long stocks;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("debtors")
    private Long debtors;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("cash_at_bank_and_in_hand")
    private Long cashAtBankAndInHand;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("investments")
    private Long investments;

    @NotNull
    @JsonProperty("total")
    private Long total;

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

    public Long getInvestments() {
        return investments;
    }

    public void setInvestments(Long investments) {
        this.investments = investments;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
