package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Debtors extends RestObject {

    private static final int MAX_FIELD_LENGTH = 20000;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("details")
    private String details;

    @JsonProperty("greater_than_one_year")
    private Long greaterThanOneYear;

    @JsonProperty("other_debtors")
    private Long otherDebtors;

    @JsonProperty("prepayments_and_accrued_income")
    private Long prepaymentsAndAccruedIncome;

    @JsonProperty("total")
    private Long total;

    @JsonProperty("trade_debtors")
    private Long tradeDebtors;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getGreaterThanOneYear() {
        return greaterThanOneYear;
    }

    public void setGreaterThanOneYear(Long greaterThanOneYear) {
        this.greaterThanOneYear = greaterThanOneYear;
    }

    public Long getOtherDebtors() {
        return otherDebtors;
    }

    public void setOtherDebtors(Long otherDebtors) {
        this.otherDebtors = otherDebtors;
    }

    public Long getPrepaymentsAndAccruedIncome() {
        return prepaymentsAndAccruedIncome;
    }

    public void setPrepaymentsAndAccruedIncome(Long prepaymentsAndAccruedIncome) {
        this.prepaymentsAndAccruedIncome = prepaymentsAndAccruedIncome;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTradeDebtors() {
        return tradeDebtors;
    }

    public void setTradeDebtors(Long tradeDebtors) {
        this.tradeDebtors = tradeDebtors;
    }
}
