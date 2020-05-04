package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amortisation {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = -99999999;
    private static final int ZERO = 0;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("at_period_end")
    private Long atPeriodEnd;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("at_period_start")
    private Long atPeriodStart;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("charge_for_year")
    private Long chargeForYear;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("on_disposals")
    private Long onDisposals;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
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