package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
<<<<<<< HEAD
<<<<<<< HEAD
=======
import javax.validation.constraints.NotNull;
>>>>>>> develop
=======
>>>>>>> 5c2af463576cc13ee5efb09d3db07c68922eaa35

@JsonInclude(Include.NON_NULL)
public class CurrentPeriod extends RestObject {

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
