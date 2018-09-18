package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class BalanceSheet {

    @NotNull
    @JsonProperty("called_up_share_capital_not_paid")
    private Integer calledUpShareCapitalNotPaid;

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

    public FixedAssets getFixedAssets() {
        return fixedAssets;
    }
}
