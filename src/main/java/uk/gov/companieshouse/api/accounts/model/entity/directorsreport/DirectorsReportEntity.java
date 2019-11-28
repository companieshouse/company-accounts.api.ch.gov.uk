package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "directors_report")
public class DirectorsReportEntity extends BaseEntity {

    @Field("data")
    private DirectorsReportDataEntity data;

    public DirectorsReportDataEntity getData() { return data; }

    public void setData(DirectorsReportDataEntity data) { this.data = data; }

    @Override
    public String toString() {
        return "DirectorsReportEntity{" +
                "data=" + data +
                "}";
    }
}
