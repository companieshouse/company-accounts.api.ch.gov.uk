package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitOrLossForFinancialYear {

    @JsonProperty("tax_on_profit")
    private Long taxOnProfit;

    @JsonProperty("total_profit_or_loss_for_financial_year")
    private Long totalProfitOrLossForFinancialYear;

    public Long getTaxOnProfit() {
        return taxOnProfit;
    }

    public void setTaxOnProfit(Long taxOnProfit) {
        this.taxOnProfit = taxOnProfit;
    }

    public Long getTotalProfitOrLossForFinancialYear() {
        return totalProfitOrLossForFinancialYear;
    }

    public void setTotalProfitOrLossForFinancialYear(Long totalProfitOrLossForFinancialYear) {
        this.totalProfitOrLossForFinancialYear = totalProfitOrLossForFinancialYear;
    }
}
