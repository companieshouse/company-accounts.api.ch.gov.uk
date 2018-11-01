package uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.fixedAssets;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TangibleAssets {

    @JsonProperty("current_amount")
    private int currentAmount;

    @JsonProperty("previous_amount")
    private int previousAmount;

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public int getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(int previousAmount) {
        this.previousAmount = previousAmount;
    }
}

