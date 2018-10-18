package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class OtherLiabilitiesOrAssetsEntity {

    @Field("prepayments_and_accrued_income")
    private Long prepaymentsAndAccruedIncome;

    @Field("creditors_due_within_one_year")
    private Long creditorsDueWithinOneYear;

    @Field("net_current_assets")
    private Long netCurrentAssets;

    @Field("total_assets_less_current_liabilities")
    private Long totalAssetsLessCurrentLiabilities;

    @Field("creditors_due_after_one_year")
    private Long creditorsDueAfterOneYear;

    @Field("provision_for_liabilities")
    private Long provisionForLiabilities;

    @Field("accruals_and_deferred_income")
    private Long accrualsAndDeferredIncome;

    @Field("total_net_assets")
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

    public Long getCreditorsDueAfterOneYear() {
        return creditorsDueAfterOneYear;
    }

    public void setCreditorsDueAfterOneYear(Long creditorsDueAfterOneYear) {
        this.creditorsDueAfterOneYear = creditorsDueAfterOneYear;
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
