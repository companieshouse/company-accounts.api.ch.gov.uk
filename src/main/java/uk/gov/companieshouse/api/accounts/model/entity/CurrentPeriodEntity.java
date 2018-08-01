package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "periods")
public class CurrentPeriodEntity extends BaseEntity {

    @Field("data")
    private CurrentPeriodDataEntity data;

    public CurrentPeriodDataEntity getData() {
        return data;
    }

    public void setData(CurrentPeriodDataEntity data) {
        this.data = data;
    }
}