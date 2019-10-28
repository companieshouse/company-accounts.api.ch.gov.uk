package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitOrLossForFinancialYear {

    @JsonProperty("tax")
    private Long tax;

    @JsonProperty("total_profit_or_loss_for_financial_year")
    private Long totalProfitOrLossForFinancialYear;

    public Long getTax() {
        return tax;
    }

    public void setTax(Long tax) {
        this.tax = tax;
    }

    public Long getTotalProfitOrLossForFinancialYear() {
        return totalProfitOrLossForFinancialYear;
    }

    public void setTotalProfitOrLossForFinancialYear(Long totalProfitOrLossForFinancialYear) {
        this.totalProfitOrLossForFinancialYear = totalProfitOrLossForFinancialYear;
    }
}
