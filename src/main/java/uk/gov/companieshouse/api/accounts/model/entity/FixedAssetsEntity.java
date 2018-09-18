package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class FixedAssetsEntity {

    @Field("tangible")
    private Integer tangible;

    @Field("total")
    private Integer totalFixedAssets;

    public Integer getTangible() {
        return tangible;
    }

    public void setTangible(Integer tangible) {
        this.tangible = tangible;
    }

    public Integer getTotalFixedAssets() {
        return totalFixedAssets;
    }

    public void setTotalFixedAssets(Integer totalFixedAssets) {
        this.totalFixedAssets = totalFixedAssets;
    }
}
