package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

public class FixedAssets {

    public static final int MAX_RANGE = 99999999;
    public static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "VALUE_OUTSIDE_RANGE")
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
        this.totalFixedAssets = totalFixedAssets;
    }
}
