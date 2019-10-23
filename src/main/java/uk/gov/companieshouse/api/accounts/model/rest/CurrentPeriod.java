package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@JsonInclude(Include.NON_NULL)
public class CurrentPeriod extends RestObject {

    @NotNull
    @JsonProperty("balance_sheet")
    @Valid
    private BalanceSheet balanceSheet;

    @NotNull
    @JsonProperty("profit_and_loss")
    @Valid
    private ProfitAndLoss profitAndLoss;

    public ProfitAndLoss getProfitAndLoss() {
        return profitAndLoss;
    }

    public void setProfitAndLoss(ProfitAndLoss profitAndLoss) {
        this.profitAndLoss = profitAndLoss;
    }

    public BalanceSheet getBalanceSheet() {
        return balanceSheet;
    }

    public void setBalanceSheet(BalanceSheet balanceSheet) {
        this.balanceSheet = balanceSheet;
    }
}
