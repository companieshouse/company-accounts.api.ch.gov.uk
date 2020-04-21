package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.intangibleassets;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class IntangibleAssetsDataEntity extends BaseDataEntity {

    @Field("goodwill")
    private IntangibleAssetsResourceEntity goodwill;

    @Field("other_intangible_assets")
    private IntangibleAssetsResourceEntity otherIntangibleAssets;

    @Field("total")
    private IntangibleAssetsResourceEntity total;

    @Field("additional_information")
    private String additionalInformation;

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public IntangibleAssetsResourceEntity getGoodwill() {
        return goodwill;
    }

    public void setGoodwill(IntangibleAssetsResourceEntity goodwill) {
        this.goodwill = goodwill;
    }

    public IntangibleAssetsResourceEntity getOtherIntangibleAssets() {
        return otherIntangibleAssets;
    }

    public void setOtherIntangibleAssets(IntangibleAssetsResourceEntity otherIntangibleAssets) {
        this.otherIntangibleAssets = otherIntangibleAssets;
    }

    public IntangibleAssetsResourceEntity getTotal() {
        return total;
    }

    public void setTotal(IntangibleAssetsResourceEntity total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "IntangibleAssetDataEntity{" +
                "additionalInformation='" + additionalInformation + "'" +
                "goodwill= '" + goodwill + "'" +
                "total= '" + total + "'" +
                "}";
    }

}
