package uk.gov.companieshouse.api.accounts.model.rest.notes.intangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cost {

    @JsonProperty("additions")
    private Long additions;

    @JsonProperty("at_period_end")
    private Long atPeriodEnd;

    @JsonProperty("at_period_start")
    private Long atPeriodStart;

    @JsonProperty("disposals")
    private Long disposals;

    @JsonProperty("revaluations")
    private Long revaluations;

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