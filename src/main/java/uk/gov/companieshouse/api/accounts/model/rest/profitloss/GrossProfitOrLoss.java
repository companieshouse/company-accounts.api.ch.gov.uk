package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrossProfitOrLoss {

    @JsonProperty("cost_of_sales")
    private Long costOfSales;

    @JsonProperty("gross_total")
    private Long grossTotal;

    @JsonProperty("turnover")
    private Long turnover;

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

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }


}
