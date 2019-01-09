package uk.gov.companieshouse.api.accounts.model.entity.notes;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.util.Objects;

public class CreditorsWithinOneYearDataEntity extends BaseDataEntity {

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

    @Field("details")
    private String details;

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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreditorsWithinOneYearDataEntity)) return false;
        CreditorsWithinOneYearDataEntity that = (CreditorsWithinOneYearDataEntity) o;
        return getAccrualsAndDeferredIncome() == that.getAccrualsAndDeferredIncome() &&
            getBankLoansAndOverdrafts() == that.getBankLoansAndOverdrafts() &&
            getFinanceLeasesAndHirePurchaseContracts() == that.getFinanceLeasesAndHirePurchaseContracts() &&
            getOtherCreditors() == that.getOtherCreditors() &&
            getTaxationAndSocialSecurity() == that.getTaxationAndSocialSecurity() &&
            getTradeCreditors() == that.getTradeCreditors() &&
            getTotal() == that.getTotal() &&
            Objects.equals(getDetails(), that.getDetails());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getAccrualsAndDeferredIncome(), getBankLoansAndOverdrafts(),
            getFinanceLeasesAndHirePurchaseContracts(), getOtherCreditors(), getTaxationAndSocialSecurity(),
            getTradeCreditors(), getTotal(), getDetails());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
