package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;

public class ProfitOrLossForFinancialYear {

    @Field("tax_on_profit")
    private Long taxOnProfit;

    @Field("total_profit_or_loss_for_financial_year")
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

    public String toString() {
        return "ProfitOrLossForFinancialYear{" +
                "taxOnProfit=" + taxOnProfit +
                ", totalProfitOrLossForFinancialYear=" + totalProfitOrLossForFinancialYear +
                '}';
    }
}
