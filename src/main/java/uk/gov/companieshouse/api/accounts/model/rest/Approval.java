package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(Include.NON_NULL)
public class Approval extends RestObject {

    private static final int MAX_FIELD_LENGTH = 120;

    @NotNull
    @PastOrPresent
    @JsonProperty("date")
    private LocalDate date;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
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
