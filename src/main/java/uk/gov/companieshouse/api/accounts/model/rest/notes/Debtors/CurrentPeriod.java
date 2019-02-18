package uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentPeriod {

    private static final int MAX_FIELD_LENGTH = 20000;
    private static final int MIN_FIELD_LENGTH = 1;
    private static final int MAX_RANGE = 99999999;
    private static final int MIN_RANGE = 0;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("details")
    private String details;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("greater_than_one_year")
    private Long greaterThanOneYear;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("other_debtors")
    private Long otherDebtors;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("prepayments_and_accrued_income")
    private Long prepaymentsAndAccruedIncome;

    @JsonProperty("total")
    private Long total;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
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
