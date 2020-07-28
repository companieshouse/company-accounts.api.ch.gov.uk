package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors;

import org.springframework.data.mongodb.core.mapping.Field;

public class LoanBreakdownResourceEntity {

    @Field("advances_credits_made")
    private Long advancesCreditsMade;

    @Field("advances_credits_repaid")
    private Long advancesCreditsRepaid;

    @Field("balance_at_period_start")
    private Long balanceAtPeriodStart;

    @Field("balance_at_period_end")
    private Long balanceAtPeriodEnd;

    public Long getAdvancesCreditsMade() {
        return advancesCreditsMade;
    }

    public void setAdvancesCreditsMade(Long advancesCreditsMade) {
        this.advancesCreditsMade = advancesCreditsMade;
    }

    public Long getAdvancesCreditsRepaid() {
        return advancesCreditsRepaid;
    }

    public void setAdvancesCreditsRepaid(Long advancesCreditsRepaid) {
        this.advancesCreditsRepaid = advancesCreditsRepaid;
    }

    public Long getBalanceAtPeriodStart() {
        return balanceAtPeriodStart;
    }

    public void setBalanceAtPeriodStart(Long balanceAtPeriodStart) {
        this.balanceAtPeriodStart = balanceAtPeriodStart;
    }

    public Long getBalanceAtPeriodEnd() {
        return balanceAtPeriodEnd;
    }

    public void setBalanceAtPeriodEnd(Long balanceAtPeriodEnd) {
        this.balanceAtPeriodEnd = balanceAtPeriodEnd;
    }

    @Override
    public String toString() {
        return "LoanBreakdownResourceEntity{" +
                "advancesCreditsMade=" + advancesCreditsMade +
                ", advancesCreditsRepaid=" + advancesCreditsRepaid +
                ", balanceAtPeriodStart=" + balanceAtPeriodStart +
                ", balanceAtPeriodEnd=" + balanceAtPeriodEnd +
                "}";
    }
}
