package uk.gov.companieshouse.api.accounts.model.entity;

import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class ApprovalDataEntity extends BaseDataEntity {

    @NotNull
    @Field("date")
    private LocalDate date;

    @Field("name")
    private String name;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ApprovalDataEntity{" +
            "date=" + date +
            ", name='" + name + '\'' +
            '}';
    }
}