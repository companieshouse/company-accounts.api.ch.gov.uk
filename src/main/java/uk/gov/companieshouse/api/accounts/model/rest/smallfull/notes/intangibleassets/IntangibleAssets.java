package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntangibleAssets extends Note {

    @Valid
    @JsonProperty("goodwill")
    private IntangibleAssetsResource goodwill;

    @Valid
    @JsonProperty("other_intangible_assets")
    private IntangibleAssetsResource otherIntangibleAssets;

    @Valid
    @JsonProperty("total")
    private IntangibleAssetsResource total;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
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
