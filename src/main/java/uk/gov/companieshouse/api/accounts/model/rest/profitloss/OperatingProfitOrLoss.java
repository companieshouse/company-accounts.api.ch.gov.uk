package uk.gov.companieshouse.api.accounts.model.rest.profitloss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperatingProfitOrLoss {

    private static final int MAX_RANGE = 99999999;
    private static final int ZERO = 0;
    private static final int MIN_RANGE = -99999999;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("administrative_expenses")
    private Long administrativeExpenses;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("distribution_costs")
    private Long distributionCosts;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("operating_total")
    private Long operatingTotal;

    @Range(min = ZERO, max = MAX_RANGE, message = "value.outside.range")
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
