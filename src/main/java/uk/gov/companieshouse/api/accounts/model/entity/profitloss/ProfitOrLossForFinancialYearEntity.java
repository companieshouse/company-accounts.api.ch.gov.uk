package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;

public class ProfitOrLossForFinancialYearEntity {

    @Field("tax")
    private Long tax;

    @Field("total_profit_or_loss_for_financial_year")
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

    public String toString() {
        return "ProfitOrLossForFinancialYear{" +
                "tax=" + tax +
                ", totalProfitOrLossForFinancialYear=" + totalProfitOrLossForFinancialYear +
                "}";
    }
}
