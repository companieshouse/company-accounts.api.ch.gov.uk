package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class CapitalAndReservesEntity {

    @Field("called_up_share_capital")
    private Long calledUpShareCapital;

    @Field("other_reserves")
    private Long otherReserves;

    @Field("profit_and_loss")
    private Long profitAndLoss;

    @Field("share_premium_account")
    private Long sharePremiumAccount;

    @Field("total_shareholders_funds")
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
