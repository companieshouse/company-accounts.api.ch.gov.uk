package uk.gov.companieshouse.api.accounts.model.entity;

import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class BalanceSheetEntity {

    @NotNull
    @Field("called_up_share_capital_not_paid")
    private Long calledUpShareCapitalNotPaid;

    @Field("fixed_assets")
    private FixedAssetsEntity fixedAssets;

    @Field("other_liabilities_or_assets")
    private OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity;

    @Field("current_assets")
    private CurrentAssetsEntity currentAssets;

    @Field("capital_and_reserves")
    private CapitalAndReservesEntity capitalAndReservesEntity;

    public Long getCalledUpShareCapitalNotPaid() {
        return calledUpShareCapitalNotPaid;
    }

    public void setCalledUpShareCapitalNotPaid(Long calledUpShareCapitalNotPaid) {
        this.calledUpShareCapitalNotPaid = calledUpShareCapitalNotPaid; }

    public FixedAssetsEntity getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(FixedAssetsEntity fixedAssets) { this.fixedAssets = fixedAssets; }

    public OtherLiabilitiesOrAssetsEntity getOtherLiabilitiesOrAssetsEntity() {
        return otherLiabilitiesOrAssetsEntity;
    }

    public void setOtherLiabilitiesOrAssetsEntity(OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity) {
        this.otherLiabilitiesOrAssetsEntity = otherLiabilitiesOrAssetsEntity;
    }

    public CurrentAssetsEntity getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(CurrentAssetsEntity currentAssets) {
        this.currentAssets = currentAssets;

    }

    public CapitalAndReservesEntity getCapitalAndReservesEntity() {
        return capitalAndReservesEntity;
    }

    public void setCapitalAndReservesEntity(CapitalAndReservesEntity capitalAndReservesEntity) {
        this.capitalAndReservesEntity = capitalAndReservesEntity;
    }
}
