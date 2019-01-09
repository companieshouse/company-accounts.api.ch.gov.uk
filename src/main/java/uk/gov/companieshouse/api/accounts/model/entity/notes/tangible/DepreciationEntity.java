package uk.gov.companieshouse.api.accounts.model.entity.notes.tangible;

import org.springframework.data.mongodb.core.mapping.Field;

public class DepreciationEntity {

    @Field("at_period_end")
    private Long atPeriodEnd;

    @Field("at_period_start")
    private Long atPeriodStart;

    @Field("charge_for_year")
    private Long chargeForYear;

    @Field("on_disposals")
    private Long onDisposables;

    @Field("other_adjustments")
    private Long otherAdjustments;

    public Long getAtPeriodEnd() {
        return atPeriodEnd;
    }

    public void setAtPeriodEnd(Long atPeriodEnd) {
        this.atPeriodEnd = atPeriodEnd;
    }

    public Long getAtPeriodStart() {
        return atPeriodStart;
    }

    public void setAtPeriodStart(Long atPeriodStart) {
        this.atPeriodStart = atPeriodStart;
    }

    public Long getChargeForYear() {
        return chargeForYear;
    }

    public void setChargeForYear(Long chargeForYear) {
        this.chargeForYear = chargeForYear;
    }

    public Long getOnDisposables() {
        return onDisposables;
    }

    public void setOnDisposables(Long onDisposables) {
        this.onDisposables = onDisposables;
    }

    public Long getOtherAdjustments() {
        return otherAdjustments;
    }

    public void setOtherAdjustments(Long otherAdjustments) {
        this.otherAdjustments = otherAdjustments;
    }

    @Override
    public String toString() {
        return "DepreciationEntity{" +
                "atPeriodEnd=" + atPeriodEnd +
                ", atPeriodStart=" + atPeriodStart +
                ", chargeForYear=" + chargeForYear +
                ", onDisposables=" + onDisposables +
                ", otherAdjustments=" + otherAdjustments +
                '}';
    }
}
