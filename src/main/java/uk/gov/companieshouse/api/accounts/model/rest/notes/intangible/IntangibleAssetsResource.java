package uk.gov.companieshouse.api.accounts.model.rest.notes.intangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;
import org.hibernate.validator.constraints.Range;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntangibleAssetsResource {

    private static final int MAX_RANGE = 999999999;
    private static final int MIN_RANGE = 0;

    @Valid
    @JsonProperty("cost")
    private Cost cost;

    @Valid
    @JsonProperty("amortisation")
    private Amortisation amortisation;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("net_book_value_at_end_of_current_period")
    private Long netBookValueAtEndOfCurrentPeriod;

    @Range(min = MIN_RANGE, max = MAX_RANGE, message = "value.outside.range")
    @JsonProperty("net_book_value_at_end_of_previous_period")
    private Long netBookValueAtEndOfPreviousPeriod;

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Amortisation getAmortisation() {
        return amortisation;
    }

    public void setAmortisation(Amortisation amortisation) {
        this.amortisation = amortisation;
    }

    public Long getNetBookValueAtEndOfCurrentPeriod() {
        return netBookValueAtEndOfCurrentPeriod;
    }

    public void setNetBookValueAtEndOfCurrentPeriod(Long netBookValueAtEndOfCurrentPeriod) {
        this.netBookValueAtEndOfCurrentPeriod = netBookValueAtEndOfCurrentPeriod;
    }

    public Long getNetBookValueAtEndOfPreviousPeriod() {
        return netBookValueAtEndOfPreviousPeriod;
    }

    public void setNetBookValueAtEndOfPreviousPeriod(Long netBookValueAtEndOfPreviousPeriod) {
        this.netBookValueAtEndOfPreviousPeriod = netBookValueAtEndOfPreviousPeriod;
    }
}
