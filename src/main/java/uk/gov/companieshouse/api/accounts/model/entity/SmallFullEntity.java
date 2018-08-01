package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "accounts")
public class SmallFullEntity extends BaseEntity{

    @Field("data")
    private SmallFullDataEntity data;

    public SmallFullDataEntity getData() {
        return data;
    }

    public void setData(SmallFullDataEntity data) {
        this.data = data;
    }
}
