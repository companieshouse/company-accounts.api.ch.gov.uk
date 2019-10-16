package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;

public class GrossProfitOrLoss {

    @Field("turnover")
    private Long turnover;

    @Field("cost_of_sales")
    private Long costOfSales;

    @Field("gross_total")
    private Long grossTotal;

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Long getCostOfSales() {
        return costOfSales;
    }

    public void setCostOfSales(Long costOfSales) {
        this.costOfSales = costOfSales;
    }

    public Long getGrossTotal() {
        return grossTotal;
    }

    public void setGrossTotal(Long grossTotal) {
        this.grossTotal = grossTotal;
    }

    @Override
    public String toString() {
        return "GrossProfitOrLoss{" +
                "turnover=" + turnover +
                ", costOfSales=" + costOfSales +
                ", grossTotal" + grossTotal +
                '}';
    }
}
