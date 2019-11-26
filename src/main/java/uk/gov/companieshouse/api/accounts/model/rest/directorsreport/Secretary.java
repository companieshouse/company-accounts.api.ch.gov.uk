package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

public class Secretary extends RestObject {

    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
