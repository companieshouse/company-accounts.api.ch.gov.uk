package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapitalAndReserves {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;
    private static final int MIN_RANGE_ONE = 1;
    private static final int MIN_RANGE_NEGATIVE = -99999999;

    @Range(min=MIN_RANGE_ONE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("called_up_share_capital")
    private Long calledUpShareCapital;

    @Range(min=MIN_RANGE_NEGATIVE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("other_reserves")
    private Long otherReserves;

    @Range(min=MIN_RANGE_NEGATIVE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("profit_and_loss")
    private Long profitAndLoss;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("share_premium_account")
    private Long sharePremiumAccount;

    @NotNull
    @JsonProperty("total_shareholders_funds")
    private Long totalShareholdersFunds;

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

    public Long getTotalShareholdersFunds() {
        return totalShareholdersFunds;
    }

    public void setTotalShareholdersFunds(Long totalShareholdersFunds) {
        this.totalShareholdersFunds = totalShareholdersFunds;
    }
}
