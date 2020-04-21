package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.intangibleassets;

import org.springframework.data.mongodb.core.mapping.Field;

public class IntangibleAssetsResourceEntity {

    @Field("cost")
    private CostEntity cost;

    @Field("amortisation")
    private AmortisationEntity amortisation;

    @Field("net_book_value_at_end_of_current_period")
    private Long netBookValueAtEndOfCurrentPeriod;

    @Field("net_book_value_at_end_of_previous_period")
    private Long netBookValueAtEndOfPreviousPeriod;

    public CostEntity getCost() {
        return cost;
    }

    public void setCost(CostEntity cost) {
        this.cost = cost;
    }

    public AmortisationEntity getAmortisation() {
        return amortisation;
    }

    public void setAmortisation(
            AmortisationEntity amortisation) {
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

    @Override
    public String toString() {
        return "IntangibleAssetsResourceEntity{" +
                "cost=" + cost +
                ", amortisation=" + amortisation +
                ", netBookValueAtEndOfCurrentPeriod=" + netBookValueAtEndOfCurrentPeriod +
                ", netBookValueAtEndOfPreviousPeriod=" + netBookValueAtEndOfPreviousPeriod +
                '}';
    }
}
