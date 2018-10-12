package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class PreviousPeriod extends RestObject {

    @NotNull(message="MANDATORY_ELEMENT_MISSING")
    @JsonProperty("balance_sheet")
    @Valid
    private BalanceSheet balanceSheet;

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }

    public void setBalanceSheet(BalanceSheet balanceSheet) {
        this.balanceSheet = balanceSheet;
    }
}


