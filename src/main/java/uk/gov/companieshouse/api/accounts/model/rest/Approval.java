package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;

@JsonInclude(Include.NON_NULL)
public class Approval extends RestObject {

    @NotNull
    @JsonProperty("date")
    private LocalDate date;

    @NotNull
    @JsonProperty("name")
    @CharSetValid
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
        return "Approval{" +
            "date=" + date +
            ", name='" + name + '\'' +
            '}';
    }
}
