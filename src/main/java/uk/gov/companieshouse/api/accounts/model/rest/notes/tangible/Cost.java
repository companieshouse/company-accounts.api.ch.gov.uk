package uk.gov.companieshouse.api.accounts.model.rest.notes.tangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cost {

    private static final int MAX_RANGE = 99999999;
    private static final int ZERO = 0;
    private static final int MIN_RANGE = -99999999;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("additions")
    private Long additions;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("at_period_end")
    private Long atPeriodEnd;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("at_period_start")
    private Long atPeriodStart;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("disposals")
    private Long disposals;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("revaluations")
    private Long revaluations;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("transfers")
    private Long transfers;

    public Long getAdditions() {
        return additions;
    }

    public void setAdditions(Long additions) {
        this.additions = additions;
    }

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

    public Long getDisposals() {
        return disposals;
    }

    public void setDisposals(Long disposals) {
        this.disposals = disposals;
    }

    public Long getRevaluations() {
        return revaluations;
    }

    public void setRevaluations(Long revaluations) {
        this.revaluations = revaluations;
    }

    public Long getTransfers() {
        return transfers;
    }

    public void setTransfers(Long transfers) {
        this.transfers = transfers;
    }
}
