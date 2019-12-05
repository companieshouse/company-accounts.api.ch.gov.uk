package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

import java.time.LocalDate;

public class Approval extends RestObject {

    @JsonProperty("name")
    private String name;

    @JsonProperty("date")
    private LocalDate date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
