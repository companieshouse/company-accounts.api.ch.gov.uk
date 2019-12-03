package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Director extends RestObject {

    @NotNull
    @JsonProperty("name")
    private String name;

    @JsonProperty("appointment_date")
    private LocalDate appointmentDate;

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
