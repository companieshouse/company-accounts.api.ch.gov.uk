package uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

public class PreviousPeriodEntity {

    @Field("accruals_and_deferred_income")
    private Long accrualsAndDeferredIncome;

    @Field("bank_loans_and_overdrafts")
    private Long bankLoansAndOverdrafts;

    @Field("finance_leases_and_hire_purchase_contracts")
    private Long financeLeasesAndHirePurchaseContracts;

    @Field("other_creditors")
    private Long otherCreditors;

    @Field("taxation_and_social_security")
    private Long taxationAndSocialSecurity;

    @Field("trade_creditors")
    private Long tradeCreditors;

    @Field("total")
    private Long total;

    public Long getAccrualsAndDeferredIncome() {
        return accrualsAndDeferredIncome;
    }

    public void setAccrualsAndDeferredIncome(Long accrualsAndDeferredIncome) {
        this.accrualsAndDeferredIncome = accrualsAndDeferredIncome;
    }

    public Long getBankLoansAndOverdrafts() {
        return bankLoansAndOverdrafts;
    }

    public void setBankLoansAndOverdrafts(Long bankLoansAndOverdrafts) {
        this.bankLoansAndOverdrafts = bankLoansAndOverdrafts;
    }

    public Long getFinanceLeasesAndHirePurchaseContracts() {
        return financeLeasesAndHirePurchaseContracts;
    }

    public void setFinanceLeasesAndHirePurchaseContracts(Long financeLeasesAndHirePurchaseContracts) {
        this.financeLeasesAndHirePurchaseContracts = financeLeasesAndHirePurchaseContracts;
    }

    public Long getOtherCreditors() {
        return otherCreditors;
    }

    public void setOtherCreditors(Long otherCreditors) {
        this.otherCreditors = otherCreditors;
    }

    public Long getTaxationAndSocialSecurity() {
        return taxationAndSocialSecurity;
    }

    public void setTaxationAndSocialSecurity(Long taxationAndSocialSecurity) {
        this.taxationAndSocialSecurity = taxationAndSocialSecurity;
    }

    public Long getTradeCreditors() {
        return tradeCreditors;
    }

    public void setTradeCreditors(Long tradeCreditors) {
        this.tradeCreditors = tradeCreditors;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof PreviousPeriodEntity)) {return false;}
        PreviousPeriodEntity that = (PreviousPeriodEntity) o;
        return Objects.equals(getAccrualsAndDeferredIncome(), that.getAccrualsAndDeferredIncome()) &&
            Objects.equals(getBankLoansAndOverdrafts(), that.getBankLoansAndOverdrafts()) &&
            Objects.equals(getFinanceLeasesAndHirePurchaseContracts(), that.getFinanceLeasesAndHirePurchaseContracts()) &&
            Objects.equals(getOtherCreditors(), that.getOtherCreditors()) &&
            Objects.equals(getTaxationAndSocialSecurity(), that.getTaxationAndSocialSecurity()) &&
            Objects.equals(getTradeCreditors(), that.getTradeCreditors()) &&
            Objects.equals(getTotal(), that.getTotal());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getAccrualsAndDeferredIncome(), getBankLoansAndOverdrafts(),
            getFinanceLeasesAndHirePurchaseContracts(), getOtherCreditors(), getTaxationAndSocialSecurity(),
            getTradeCreditors(), getTotal());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
