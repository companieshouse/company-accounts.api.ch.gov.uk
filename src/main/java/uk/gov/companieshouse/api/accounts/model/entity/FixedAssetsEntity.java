package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class FixedAssetsEntity {

    @Field("tangible")
    private Long tangible;

    @Field("total")
    private Long totalFixedAssets;

    public Long getTangible() {
        return tangible;
    }

    public void setTangible(Long tangible) {
        this.tangible = tangible;
    }

    public Long getTotalFixedAssets() {
        return totalFixedAssets;
    }

    public void setTotalFixedAssets(Long totalFixedAssets) {
        this.totalFixedAssets = totalFixedAssets;
    }
}
