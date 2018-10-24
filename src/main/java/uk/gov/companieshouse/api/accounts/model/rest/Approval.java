package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(Include.NON_NULL)
public class Approval extends RestObject {

    @NotNull
    @PastOrPresent
    @JsonProperty("date")
    private LocalDate date;

    @NotBlank
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @JsonProperty("name")
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
