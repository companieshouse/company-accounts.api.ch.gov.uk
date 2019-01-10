package uk.gov.companieshouse.api.accounts.model.entity.notes.tangible;

import org.springframework.data.mongodb.core.mapping.Field;

public class CostEntity {

    @Field("additions")
    private Long additions;

    @Field("at_period_end")
    private Long atPeriodEnd;

    @Field("at_period_start")
    private Long atPeriodStart;

    @Field("disposals")
    private Long disposals;

    @Field("revaluations")
    private Long revaluations;

    @Field("transfers")
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

    @Override
    public String toString() {
        return "CostEntity{" +
                "additions=" + additions +
                ", atPeriodEnd=" + atPeriodEnd +
                ", atPeriodStart=" + atPeriodStart +
                ", disposals=" + disposals +
                ", revaluations=" + revaluations +
                ", transfers=" + transfers +
                '}';
    }
}
