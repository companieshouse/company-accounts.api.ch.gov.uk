package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitOrLossForFinancialYear {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = -99999999;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("tax")
    private Long tax;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
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
