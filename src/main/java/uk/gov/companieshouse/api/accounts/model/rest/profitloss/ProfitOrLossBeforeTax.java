package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfitOrLossBeforeTax {

    private static final int MAX_RANGE = 99999999;
    private static final int ZERO = 0;
    private static final int MIN_RANGE = -99999999;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("interest_payable_and_similar_charges")
    private Long interestPayableAndSimilarCharges;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("interest_receivable_and_similar_income")
    private Long interestReceivableAndSimilarIncome;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
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
