package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitOrLossBeforeTax {

    @JsonProperty("interest_payable_and_similar_charges")
    private Long interestPayableAndSimilarCharges;

    @JsonProperty("interest_receivable_and_similar_income")
    private Long interestReceivableAndSimilarIncome;

    @JsonProperty("total_profit_or_loss_before_tax")
    private Long totalProfitOrLossBeforeTax;

    public Long getInterestPayableAndSimilarCharges() {
        return interestPayableAndSimilarCharges;
    }

    public void setInterestPayableAndSimilarCharges(Long interestPayableAndSimilarCharges) {
        this.interestPayableAndSimilarCharges = interestPayableAndSimilarCharges;
    }

    public Long getInterestReceivableAndSimilarIncome() {
        return interestReceivableAndSimilarIncome;
    }

    public void setInterestReceivableAndSimilarIncome(Long interestReceivableAndSimilarIncome) {
        this.interestReceivableAndSimilarIncome = interestReceivableAndSimilarIncome;
    }

    public Long getTotalProfitOrLossBeforeTax() {
        return totalProfitOrLossBeforeTax;
    }

    public void setTotalProfitOrLossBeforeTax(Long totalProfitOrLossBeforeTax) {
        this.totalProfitOrLossBeforeTax = totalProfitOrLossBeforeTax;
    }
}
