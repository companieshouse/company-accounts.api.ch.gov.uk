package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapitalAndReserves {

    @JsonProperty("called_up_share_capital")
    private Long calledUpShareCapital;

    @JsonProperty("other_reserves")
    private Long otherReserves;

    @JsonProperty("profit_and_loss")
    private Long profitAndLoss;

    @JsonProperty("share_premium_account")
    private Long sharePremiumAccount;

    @JsonProperty("total_shareholder_funds")
    private Long totalShareholderFunds;

    public Long getCalledUpShareCapital() {
        return calledUpShareCapital;
    }

    public void setCalledUpShareCapital(Long calledUpShareCapital) {
        this.calledUpShareCapital = calledUpShareCapital;
    }

    public Long getOtherReserves() {
        return otherReserves;
    }

    public void setOtherReserves(Long otherReserves) {
        this.otherReserves = otherReserves;
    }

    public Long getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(Long profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    public Long getSharePremiumAccount() {
        return sharePremiumAccount;
    }

    public void setSharePremiumAccount(Long sharePremiumAccount) {
        this.sharePremiumAccount = sharePremiumAccount;
    }

    public Long getTotalShareholderFunds() {
        return totalShareholderFunds;
    }

    public void setTotalShareholderFunds(Long totalShareholderFunds) {
        this.totalShareholderFunds = totalShareholderFunds;
    }
}
