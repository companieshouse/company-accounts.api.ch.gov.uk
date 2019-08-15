package uk.gov.companieshouse.api.accounts.model.rest.notes.intangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amortisation {

    @JsonProperty("at_period_end")
    private Long atPeriodEnd;

    @JsonProperty("at_period_start")
    private Long atPeriodStart;

    @JsonProperty("charge_for_year")
    private Long chargeForYear;

    @JsonProperty("on_disposals")
    private Long onDisposals;

    @JsonProperty("other_adjustments")
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

    public Long getOnDisposals() {
        return onDisposals;
    }

    public void setOnDisposals(Long onDisposals) {
        this.onDisposals = onDisposals;
    }

    public Long getOtherAdjustments() {
        return otherAdjustments;
    }

    public void setOtherAdjustments(Long otherAdjustments) {
        this.otherAdjustments = otherAdjustments;
    }
}