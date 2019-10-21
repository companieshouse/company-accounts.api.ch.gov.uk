package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;

public class OperatingProfitOrLossEntity {

    @Field("distribution_costs")
    private Long distributionCosts;

    @Field("administrative_expenses")
    private Long administrativeExpenses;

    @Field("other_operating_income")
    private Long otherOperatingIncome;

    @Field("operating_total")
    private Long operatingTotal;

    public Long getDistributionCosts() {
        return distributionCosts;
    }

    public void setDistributionCosts(Long distributionCosts) {
        this.distributionCosts = distributionCosts;
    }

    public Long getAdministrativeExpenses() {
        return administrativeExpenses;
    }

    public void setAdministrativeExpenses(Long administrativeExpenses) {
        this.administrativeExpenses = administrativeExpenses;
    }

    public Long getOtherOperatingIncome() {
        return otherOperatingIncome;
    }

    public void setOtherOperatingIncome(Long otherOperatingIncome) {
        this.otherOperatingIncome = otherOperatingIncome;
    }

    public Long getOperatingTotal() {
        return operatingTotal;
    }

    public void setOperatingTotal(Long operatingTotal) {
        this.operatingTotal = operatingTotal;
    }


    @Override
    public String toString() {
        return "OperatingProfitOrLoss{" +
                "distributionCosts=" + distributionCosts +
                ", administrativeExpenses=" + administrativeExpenses +
                ", otherOperatingIncome=" + otherOperatingIncome +
                ", operatingTotal=" + operatingTotal +
                '}';

    }
}
