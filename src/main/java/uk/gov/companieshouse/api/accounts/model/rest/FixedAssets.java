package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FixedAssets {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("tangible")
    private Long tangible;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("investments")
    private Long investments;

    @JsonProperty("total")
    private Long total;

    public Long getTangible() {
        return tangible;
    }

    public void setTangible(Long tangible) {
        this.tangible = tangible;
    }

    public Long getInvestments() {
        return investments;
    }

    public void setInvestments(Long investments) {
        this.investments = investments;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
