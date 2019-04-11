package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic_report")
public class CicReportEntity extends BaseEntity {

    @Field("data")
    private CicReportDataEntity data;

    public CicReportDataEntity getData() {
        return data;
    }

    public void setData(CicReportDataEntity data) {
        this.data = data;
    }
}
