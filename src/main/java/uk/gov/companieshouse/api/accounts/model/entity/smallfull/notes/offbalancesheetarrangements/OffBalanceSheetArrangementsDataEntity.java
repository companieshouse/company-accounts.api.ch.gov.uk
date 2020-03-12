package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class OffBalanceSheetArrangementsDataEntity extends BaseDataEntity {

    @Field("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "OffBalanceSheetArrangementsDataEntity{" +
                "details='" + details + '\'' +
                '}';
    }
}
