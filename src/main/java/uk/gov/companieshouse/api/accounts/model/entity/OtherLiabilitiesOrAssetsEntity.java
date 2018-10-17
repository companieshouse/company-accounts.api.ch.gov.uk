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

    @Field("provision_for_liabilities")
    private Long provisionForLiabilities;

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

    public Long getProvisionForLiabilities() {
        return provisionForLiabilities;
    }

    public void setProvisionForLiabilities(Long provisionForLiabilities) {
        this.provisionForLiabilities = provisionForLiabilities;
    }
}
