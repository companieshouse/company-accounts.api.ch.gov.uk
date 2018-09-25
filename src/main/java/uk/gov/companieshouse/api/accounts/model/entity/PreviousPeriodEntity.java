package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "periods")
public class PreviousPeriodEntity extends BaseEntity {

    @Field("data")
    private PreviousPeriodDataEntity data;

    public PreviousPeriodDataEntity getData() {
        return data;
    }

    public void setData(PreviousPeriodDataEntity data) {
        this.data = data;
    }
}
