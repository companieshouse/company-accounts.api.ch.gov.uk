package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class MembersFundsEntity {

    @Field("profit_and_loss_account")
    private Long profitAndLossAccount;

    @Field("total_members_funds")
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
