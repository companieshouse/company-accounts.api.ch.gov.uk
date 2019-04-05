package uk.gov.companieshouse.api.accounts.model.entity;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "cic34_report")
public class Cic34ReportEntity extends BaseEntity {

    @Field("data")
    private Cic34ReportDataEntity data;

    public Cic34ReportDataEntity getData() {
        return data;
    }

    public void setData(Cic34ReportDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() { return new Gson().toJson(this); }
}
