package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import org.springframework.data.mongodb.core.mapping.Field;

public class RptTransactionBreakdownEntity {

    @Field("balance_at_period_start")
    private Long balanceAtPeriodStart;

    @Field("balance_at_period_end")
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

    @Override
    public String toString() {
        return "RptTransactionBreakdownEntity{" +
                "balanceAtPeriodStart=" + balanceAtPeriodStart +
                ", balanceAtPeriodEnd=" + balanceAtPeriodEnd +
                "}";
    }
}
