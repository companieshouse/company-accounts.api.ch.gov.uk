package uk.gov.companieshouse.api.accounts.model.rest.notes.intangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntangibleAssets extends RestObject {

    @JsonProperty("goodwill")
    private IntangibleAssetsResource goodwill;

    @JsonProperty("other_intangible_assets")
    private IntangibleAssetsResource otherIntangibleAssets;

    @JsonProperty("total")
    private IntangibleAssetsResource total;

    @JsonProperty("additional_information")
    private String additionalInformation;

    public IntangibleAssetsResource getGoodwill() {
        return goodwill;
    }

    public void setGoodwill(IntangibleAssetsResource goodwill) {
        this.goodwill = goodwill;
    }

    public IntangibleAssetsResource getOtherIntangibleAssets() {
        return otherIntangibleAssets;
    }

    public void setOtherIntangibleAssets(IntangibleAssetsResource otherIntangibleAssets) {
        this.otherIntangibleAssets = otherIntangibleAssets;
    }

    public IntangibleAssetsResource getTotal() {
        return total;
    }

    public void setTotal(IntangibleAssetsResource total) {
        this.total = total;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
