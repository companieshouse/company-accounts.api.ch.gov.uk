package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic_report")
public class CicReportStatementsEntity extends BaseEntity {

    @Field("data")
    private CicReportStatementsDataEntity data;

    public CicReportStatementsDataEntity getData() {
        return data;
    }

    public void setData(CicReportStatementsDataEntity data) {
        this.data = data;
    }
}
