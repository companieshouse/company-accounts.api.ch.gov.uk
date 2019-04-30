package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class MembersFunds {

    @JsonProperty("profit_and_loss_account")
    private Long profitAndLossAccount;

    @JsonProperty("total_members_funds")
    private Long totalMembersFunds;

    public Long getProfitAndLossAccount() {
        return profitAndLossAccount;
    }

    public void setProfitAndLossAccount(Long profitAndLossAccount) {
        this.profitAndLossAccount = profitAndLossAccount;
    }

    public Long getTotalMembersFunds() {
        return totalMembersFunds;
    }

    public void setTotalMembersFunds(Long totalMembersFunds) {
        this.totalMembersFunds = totalMembersFunds;
    }
}
