package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

public class DirectorEntity extends BaseEntity {

    @Field("data")
    private DirectorDataEntity data;

    public DirectorDataEntity getData() { return data; }

    public void setData(DirectorDataEntity data) { this.data = data; }

    @Override
    public String toString() {
        return "DirectorEntity{" +
                "data=" + data +
                "}";
    }
}
