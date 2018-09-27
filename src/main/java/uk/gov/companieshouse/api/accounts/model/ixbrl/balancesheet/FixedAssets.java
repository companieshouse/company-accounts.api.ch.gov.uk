package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FixedAssets {

    @JsonProperty("tangible")
    public TangibleAssets tangibleAssets;

    @JsonProperty("current_total")
    public int totalFixedAssetsCurrent;

    @JsonProperty("previous_total")
    public int totalFixedAssetsPrevious;

    public TangibleAssets getTangibleAssets() {
        return tangibleAssets;
    }

    public void setTangibleAssets( TangibleAssets tangibleAssets) {
        this.tangibleAssets = tangibleAssets;
    }

    public int getTotalFixedAssetsCurrent() {
        return totalFixedAssetsCurrent;
    }

    public void setTotalFixedAssetsCurrent(int totalFixedAssetsCurrent) {
        this.totalFixedAssetsCurrent = totalFixedAssetsCurrent;
    }

    public int getTotalFixedAssetsPrevious() {
        return totalFixedAssetsPrevious;
    }

    public void setTotalFixedAssetsPrevious(int totalFixedAssetsPrevious) {
        this.totalFixedAssetsPrevious = totalFixedAssetsPrevious;
    }
}
