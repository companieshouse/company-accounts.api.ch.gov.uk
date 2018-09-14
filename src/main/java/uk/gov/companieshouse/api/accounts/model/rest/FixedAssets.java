package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FixedAssets {

    @JsonProperty("tangible")
    private Integer tangible;

    @JsonProperty("total")
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
        this.totalFixedAssets = totalFixedAssets; }
}
