package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "approvals")
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
            '}';
    }
}
