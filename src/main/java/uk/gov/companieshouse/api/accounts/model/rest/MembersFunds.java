package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@JsonInclude(Include.NON_NULL)
public class MembersFunds {

    private static final int MIN_RANGE = -99999999;
    private static final int MAX_RANGE = 99999999;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("profit_and_loss_account")
    private Long profitAndLossAccount;

    @NotNull
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
