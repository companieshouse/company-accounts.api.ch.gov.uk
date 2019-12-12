package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.util.Map;

public class DirectorsReportDataEntity extends BaseDataEntity {

    @Field("directors")
    private Map<String, String> directors;

    public Map<String, String> getDirectors() {
        return directors;
    }

    public void setDirectors(Map<String, String> directors) {
        this.directors = directors;
    }

    @Override public String toString() {
        return "DirectorsReportDataEntity{" +
                "directors=" + directors +
                "}";
    }

}
