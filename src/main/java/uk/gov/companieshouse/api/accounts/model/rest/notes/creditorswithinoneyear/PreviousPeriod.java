package uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviousPeriod {

    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("accruals_and_deferred_income")
    private Long accrualsAndDeferredIncome;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("bank_loans_and_overdrafts")
    private Long bankLoansAndOverdrafts;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("finance_leases_and_hire_purchase_contracts")
    private Long financeLeasesAndHirePurchaseContracts;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("other_creditors")
    private Long otherCreditors;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("taxation_and_social_security")
    private Long taxationAndSocialSecurity;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("trade_creditors")
    private Long tradeCreditors;

    @JsonProperty("total")
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
        if (!(o instanceof PreviousPeriod)) {return false;}
        PreviousPeriod that = (PreviousPeriod) o;
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
