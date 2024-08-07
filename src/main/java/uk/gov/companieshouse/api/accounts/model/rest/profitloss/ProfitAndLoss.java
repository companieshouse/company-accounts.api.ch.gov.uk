package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitAndLoss extends RestObject {

    @Valid
    @JsonProperty("gross_profit_or_loss")
    private GrossProfitOrLoss grossProfitOrLoss;

    @Valid
    @JsonProperty("operating_profit_or_loss")
    private OperatingProfitOrLoss operatingProfitOrLoss;

    @Valid
    @JsonProperty("profit_or_loss_before_tax")
    private ProfitOrLossBeforeTax profitOrLossBeforeTax;

    @Valid
    @JsonProperty("profit_or_loss_for_financial_year")
    private ProfitOrLossForFinancialYear profitOrLossForFinancialYear;

    public GrossProfitOrLoss getGrossProfitOrLoss() {
        return grossProfitOrLoss;
    }

    public void setGrossProfitOrLoss(GrossProfitOrLoss grossProfitOrLoss) {
        this.grossProfitOrLoss = grossProfitOrLoss;
    }

    public OperatingProfitOrLoss getOperatingProfitOrLoss() {
        return operatingProfitOrLoss;
    }

    public void setOperatingProfitOrLoss(OperatingProfitOrLoss operatingProfitOrLoss) {
        this.operatingProfitOrLoss = operatingProfitOrLoss;
    }

    public ProfitOrLossBeforeTax getProfitOrLossBeforeTax() {
        return profitOrLossBeforeTax;
    }

    public void setProfitOrLossBeforeTax(ProfitOrLossBeforeTax profitOrLossBeforeTax) {
        this.profitOrLossBeforeTax = profitOrLossBeforeTax;
    }

    public ProfitOrLossForFinancialYear getProfitOrLossForFinancialYear() {
        return profitOrLossForFinancialYear;
    }

    public void setProfitOrLossForFinancialYear(ProfitOrLossForFinancialYear profitOrLossForFinancialYear) {
        this.profitOrLossForFinancialYear = profitOrLossForFinancialYear;
    }

}
