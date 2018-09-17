package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;

public class BalanceSheet {

    public static final int MAX_RANGE = 99999999;
    public static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "VALUE_OUTSIDE_RANGE")
    @JsonProperty("called_up_share_capital_not_paid")
    private Integer calledUpShareCapitalNotPaid;

    @Valid
    @JsonProperty("fixed_assets")
    private FixedAssets fixedAssets;

    public Integer getCalledUpShareCapitalNotPaid() {
        return calledUpShareCapitalNotPaid;
    }

    public void setCalledUpShareCapitalNotPaid(Integer calledUpShareCapitalNotPaid) {
        this.calledUpShareCapitalNotPaid = calledUpShareCapitalNotPaid;
    }

    public void setFixedAssets(FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public FixedAssets getFixedAssets() { return fixedAssets; }
}
