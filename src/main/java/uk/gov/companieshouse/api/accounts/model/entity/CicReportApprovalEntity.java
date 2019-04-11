package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic_report")
public class CicReportApprovalEntity extends BaseEntity {

    @Field("data")
    private CicReportApprovalDataEntity data;

    public CicReportApprovalDataEntity getData() {
        return data;
    }

    public void setData(CicReportApprovalDataEntity data) {
        this.data = data;
    }
}
