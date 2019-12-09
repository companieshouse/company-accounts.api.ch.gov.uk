package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import uk.gov.companieshouse.api.accounts.validation.WithinCurrentPeriod;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Director extends RestObject {

    private static final int MAX_FIELD_LENGTH = 120;
    private static final int MIN_FIELD_LENGTH = 1;

    @NotNull
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("name")
    private String name;

    @WithinCurrentPeriod
    @JsonProperty("appointment_date")
    private LocalDate appointmentDate;

    @WithinCurrentPeriod
    @JsonProperty("resignation_date")
    private LocalDate resignationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

}
