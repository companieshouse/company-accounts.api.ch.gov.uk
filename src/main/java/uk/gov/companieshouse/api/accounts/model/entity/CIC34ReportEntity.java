package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic34_report")
public class CIC34ReportEntity extends BaseEntity {

    @Field("data")
    private CIC34ReportDataEntity data;

    public CIC34ReportDataEntity getData() {
        return data;
    }

    public void setData(CIC34ReportDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CIC34ReportEntity {" +
                "data=" + data +
                "}";
    }
}
