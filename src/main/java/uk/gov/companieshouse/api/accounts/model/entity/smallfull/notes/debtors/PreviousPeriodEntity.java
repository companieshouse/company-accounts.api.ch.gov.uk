package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.debtors;

import org.springframework.data.mongodb.core.mapping.Field;

public class PreviousPeriodEntity {

    @Field("greater_than_one_year")
    private Long greaterThanOneYear;

    @Field("other_debtors")
    private Long otherDebtors;

    @Field("prepayments_and_accrued_income")
    private Long prepaymentsAndAccruedIncome;

    @Field("total")
    private Long total;

    @Field("trade_debtors")
    private Long tradeDebtors;

    public Long getGreaterThanOneYear() {
        return greaterThanOneYear;
    }

    public void setGreaterThanOneYear(Long greaterThanOneYear) {
        this.greaterThanOneYear = greaterThanOneYear;
    }

    public Long getOtherDebtors() {
        return otherDebtors;
    }

    public void setOtherDebtors(Long otherDebtors) {
        this.otherDebtors = otherDebtors;
    }

    public Long getPrepaymentsAndAccruedIncome() {
        return prepaymentsAndAccruedIncome;
    }

    public void setPrepaymentsAndAccruedIncome(Long prepaymentsAndAccruedIncome) {
        this.prepaymentsAndAccruedIncome = prepaymentsAndAccruedIncome;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTradeDebtors() {
        return tradeDebtors;
    }

    public void setTradeDebtors(Long tradeDebtors) {
        this.tradeDebtors = tradeDebtors;
    }
}
