package uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Range;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentPeriod {

    private static final int MAX_FIELD_LENGTH = 20000;
    private static final int MIN_FIELD_LENGTH = 1;
    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("bank_loans_and_overdrafts")
    private Long bankLoansAndOverdrafts;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("finance_leases_and_hire_purchase_contracts")
    private Long financeLeasesAndHirePurchaseContracts;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
    @JsonProperty("other_creditors")
    private Long otherCreditors;

    @JsonProperty("total")
    private Long total;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("details")
    private String details;

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
}
