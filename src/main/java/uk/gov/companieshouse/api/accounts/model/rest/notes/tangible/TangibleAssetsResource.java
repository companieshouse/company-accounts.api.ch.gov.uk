package uk.gov.companieshouse.api.accounts.model.rest.notes.tangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TangibleAssetsResource {

    @JsonProperty("cost")
    private Cost cost;

    @JsonProperty("depreciation")
    private Depreciation depreciation;

    @JsonProperty("net_book_value_at_end_of_current_period")
    private Long netBookValueAtEndOfCurrentPeriod;

    @JsonProperty("net_book_value_at_end_of_previous_period")
    private Long netBookValueAtEndOfPreviousPeriod;

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public Depreciation getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(Depreciation depreciation) {
        this.depreciation = depreciation;
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
