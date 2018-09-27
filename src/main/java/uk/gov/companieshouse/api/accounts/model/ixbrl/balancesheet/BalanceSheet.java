package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;

public class BalanceSheet {

    @JsonProperty("current_period_date_formatted")
    private String currentPeriodDateFormatted;
    @JsonProperty("previous_period_date_formatted")
    private String previousPeriodDateFormatted;
    @JsonProperty("called_up_shared_capital_not_paid")
    private CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid;
    @JsonProperty("fixed_assets")
    private FixedAssets fixedAssets;

    public String getCurrentPeriodDateFormatted() {
        return currentPeriodDateFormatted;
    }

    public void setCurrentPeriodDateFormatted(String currentPeriodDateFormatted) {
        this.currentPeriodDateFormatted = currentPeriodDateFormatted;
    }

    public String getPreviousPeriodDateFormatted() {
        return previousPeriodDateFormatted;
    }

    public void setPreviousPeriodDateFormatted(String previousPeriodDateFormatted) {
        this.previousPeriodDateFormatted = previousPeriodDateFormatted;
    }

    public CalledUpSharedCapitalNotPaid getCalledUpSharedCapitalNotPaid() {
        return calledUpSharedCapitalNotPaid;
    }

    public void setCalledUpSharedCapitalNotPaid(
        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid) {
        this.calledUpSharedCapitalNotPaid = calledUpSharedCapitalNotPaid;
    }

    public FixedAssets getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(FixedAssets fixedAssets) {
        this.fixedAssets = fixedAssets;
    }
}
