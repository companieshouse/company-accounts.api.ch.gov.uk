package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanBreakdownResource {

    private static final int MIN_RANGE = 0;
    private static final int MAX_RANGE = 99999999;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("advances_credits_made")
    private Long advancesCreditsMade;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("advances_credits_repaid")
    private Long advancesCreditsRepaid;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("balance_at_period_start")
    private Long balanceAtPeriodStart;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("balance_at_period_end")
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

}
