package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;

@JsonInclude(Include.NON_NULL)
public class BalanceSheet {

    public static final int MAX_RANGE = 99999999;
    public static final int MIN_RANGE = 0;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("called_up_share_capital_not_paid")
    private Long calledUpShareCapitalNotPaid;

    @Valid
    @JsonProperty("fixed_assets")
    private FixedAssets fixedAssets;

    @Valid
    @JsonProperty("other_liabilities_or_assets")
    private OtherLiabilitiesOrAssets otherLiabilitiesOrAssets;

    @Valid
    @JsonProperty("current_assets")
    private CurrentAssets currentAssets;

    @JsonProperty("capital_and_reserves")
    private CapitalAndReserves capitalAndReserves;

    public Long getCalledUpShareCapitalNotPaid() {
        return calledUpShareCapitalNotPaid;
    }

    public void setCalledUpShareCapitalNotPaid(Long calledUpShareCapitalNotPaid) {
        this.calledUpShareCapitalNotPaid = calledUpShareCapitalNotPaid;
    }

    public void setFixedAssets(FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public FixedAssets getFixedAssets() {
        return fixedAssets;
    }

    public OtherLiabilitiesOrAssets getOtherLiabilitiesOrAssets() {
        return otherLiabilitiesOrAssets;
    }

    public void setOtherLiabilitiesOrAssets(
            OtherLiabilitiesOrAssets otherLiabilitiesOrAssets) {
        this.otherLiabilitiesOrAssets = otherLiabilitiesOrAssets;
    }

    public CurrentAssets getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(CurrentAssets currentAssets) {
        this.currentAssets = currentAssets;
    }

    public CapitalAndReserves getCapitalAndReserves() {
        return capitalAndReserves;
    }

    public void setCapitalAndReserves(CapitalAndReserves capitalAndReserves) {
        this.capitalAndReserves = capitalAndReserves;
    }
}
