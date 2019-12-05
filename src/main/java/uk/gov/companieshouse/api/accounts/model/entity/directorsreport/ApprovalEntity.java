package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "directors_report")
public class ApprovalEntity extends BaseEntity {

    @Field("data")
    private ApprovalDataEntity data;

    public ApprovalDataEntity getData() {
        return data;
    }

    public void setData(ApprovalDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApprovalEntity{" +
                "data=" + data +
                "}";
    }
}
