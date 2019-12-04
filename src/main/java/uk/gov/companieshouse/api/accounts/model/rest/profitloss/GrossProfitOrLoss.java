package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrossProfitOrLoss {

    private static final int MAX_RANGE = 99999999;
    private static final int ZERO = 0;
    private static final int MIN_RANGE = -99999999;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("cost_of_sales")
    private Long costOfSales;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("gross_total")
    private Long grossTotal;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
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
