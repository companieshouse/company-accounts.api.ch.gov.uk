package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;

public class ProfitOrLossBeforeTax {

    @Field("interest_receivable_and_similar_income")
    private Long interestReceivableAndSimilarIncome;

    @Field("interest_payable_and_similar_charges")
    private Long interestPayableAndSimilarCharges;

    @Field("total_profit_or_loss_before_tax")
    private Long totalProfitOrLossBeforeTax;

    public Long getInterestReceivableAndSimilarIncome() {
        return interestReceivableAndSimilarIncome;
    }

    public void setInterestReceivableAndSimilarIncome(Long interestReceivableAndSimilarIncome) {
        this.interestReceivableAndSimilarIncome = interestReceivableAndSimilarIncome;
    }

    public Long getInterestPayableAndSimilarCharges() {
        return interestPayableAndSimilarCharges;
    }

    public void setInterestPayableAndSimilarCharges(Long interestPayableAndSimilarCharges) {
        this.interestPayableAndSimilarCharges = interestPayableAndSimilarCharges;
    }

    public Long getTotalProfitOrLossBeforeTax() {
        return totalProfitOrLossBeforeTax;
    }

    public void setTotalProfitOrLossBeforeTax(Long totalProfitOrLossBeforeTax) {
        this.totalProfitOrLossBeforeTax = totalProfitOrLossBeforeTax;
    }

    public String toString() {
        return "ProfitOrLossBeforeTax{" +
                "interestReceivableAndSimilarIncome=" + interestReceivableAndSimilarIncome +
                ", interestPayableAndSimilarChanges=" + interestPayableAndSimilarCharges +
                ", totalProfitOrLossBeforeTax" +
                '}';
    }
}
