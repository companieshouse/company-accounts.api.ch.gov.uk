package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RptTransactionBreakdown {

    private static final int MIN_RANGE = 0;
    private static final int MAX_RANGE = 99999999;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("balance_at_period_start")
    private Long balanceAtPeriodStart;

    @NotNull
    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("balance_at_period_end")
    private Long balanceAtPeriodEnd;

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
