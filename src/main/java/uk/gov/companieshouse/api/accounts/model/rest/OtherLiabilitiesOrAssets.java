package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtherLiabilitiesOrAssets {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("prepayments_and_accrued_income")
    private Long prepaymentsAndAccruedIncome;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("creditors_due_within_one_year")
    private Long creditorsDueWithinOneYear;

    @JsonProperty("net_current_assets")
    private Long netCurrentAssets;

    @JsonProperty("total_assets_less_current_liabilities")
    private Long totalAssetsLessCurrentLiabilities;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("creditors_after_one_year")
    private Long creditorsAfterOneYear;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("provision_for_liabilities")
    private Long provisionForLiabilities;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("accruals_and_deferred_income")
    private Long accrualsAndDeferredIncome;

    @NotNull
    @JsonProperty("total_net_assets")
    private Long totalNetAssets;

    public Long getPrepaymentsAndAccruedIncome() {
        return prepaymentsAndAccruedIncome;
    }

    public void setPrepaymentsAndAccruedIncome(Long prepaymentsAndAccruedIncome) {
        this.prepaymentsAndAccruedIncome = prepaymentsAndAccruedIncome;
    }

    public Long getCreditorsDueWithinOneYear() {
        return creditorsDueWithinOneYear;
    }

    public void setCreditorsDueWithinOneYear(Long creditorsDueWithinOneYear) {
        this.creditorsDueWithinOneYear = creditorsDueWithinOneYear;
    }

    public Long getNetCurrentAssets() {
        return netCurrentAssets;
    }

    public void setNetCurrentAssets(Long netCurrentAssets) {
        this.netCurrentAssets = netCurrentAssets;
    }

    public Long getTotalAssetsLessCurrentLiabilities() {
        return totalAssetsLessCurrentLiabilities;
    }

    public void setTotalAssetsLessCurrentLiabilities(Long totalAssetsLessCurrentLiabilities) {
        this.totalAssetsLessCurrentLiabilities = totalAssetsLessCurrentLiabilities;
    }

    public Long getCreditorsAfterOneYear() {
        return creditorsAfterOneYear;
    }

    public void setCreditorsAfterOneYear(Long creditorsAfterOneYear) {
        this.creditorsAfterOneYear = creditorsAfterOneYear;
    }

    public Long getProvisionForLiabilities() {
        return provisionForLiabilities;
    }

    public void setProvisionForLiabilities(Long provisionForLiabilities) {
        this.provisionForLiabilities = provisionForLiabilities;
    }

    public Long getAccrualsAndDeferredIncome() {
        return accrualsAndDeferredIncome;
    }

    public void setAccrualsAndDeferredIncome(Long accrualsAndDeferredIncome) {
        this.accrualsAndDeferredIncome = accrualsAndDeferredIncome;
    }

    public Long getTotalNetAssets() {
        return totalNetAssets;
    }

    public void setTotalNetAssets(Long totalNetAssets) {
        this.totalNetAssets = totalNetAssets;
    }
}