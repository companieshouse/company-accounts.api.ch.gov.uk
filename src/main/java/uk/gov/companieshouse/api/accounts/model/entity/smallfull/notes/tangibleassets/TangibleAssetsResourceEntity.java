package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.tangibleassets;

import org.springframework.data.mongodb.core.mapping.Field;

public class TangibleAssetsResourceEntity {

    @Field("cost")
    private CostEntity cost;

    @Field("depreciation")
    private DepreciationEntity depreciation;

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

    public DepreciationEntity getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(
            DepreciationEntity depreciation) {
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

    @Override
    public String toString() {
        return "TangibleAssetsResourceEntity{" +
                "cost=" + cost +
                ", depreciation=" + depreciation +
                ", netBookValueAtEndOfCurrentPeriod=" + netBookValueAtEndOfCurrentPeriod +
                ", netBookValueAtEndOfPreviousPeriod=" + netBookValueAtEndOfPreviousPeriod +
                '}';
    }
}
