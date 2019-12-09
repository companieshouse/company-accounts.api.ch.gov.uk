package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.time.LocalDate;

public class DirectorsApprovalDataEntity extends BaseDataEntity {

    @Field("name")
    private String name;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Field("date")
    private LocalDate date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
