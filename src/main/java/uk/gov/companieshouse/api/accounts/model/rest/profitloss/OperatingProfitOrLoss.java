package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperatingProfitOrLoss {

    @JsonProperty("administrative_expenses")
    private Long administrativeExpenses;

    @JsonProperty("distribution_costs")
    private Long distributionCosts;

    @JsonProperty("operating_total")
    private Long operatingTotal;

    @JsonProperty("other_operating_income")
    private Long otherOperatingIncome;

    public Long getAdministrativeExpenses() {
        return administrativeExpenses;
    }

    public void setAdministrativeExpenses(Long administrativeExpenses) {
        this.administrativeExpenses = administrativeExpenses;
    }

    public Long getDistributionCosts() {
        return distributionCosts;
    }

    public void setDistributionCosts(Long distributionCosts) {
        this.distributionCosts = distributionCosts;
    }

    public Long getOperatingTotal() {
        return operatingTotal;
    }

    public void setOperatingTotal(Long operatingTotal) {
        this.operatingTotal = operatingTotal;
    }

    public Long getOtherOperatingIncome() {
        return otherOperatingIncome;
    }

    public void setOtherOperatingIncome(Long otherOperatingIncome) {
        this.otherOperatingIncome = otherOperatingIncome;
    }
}
