package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Secretary extends RestObject {

    private static final int MAX_FIELD_LENGTH = 120;

    @NotNull
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @JsonProperty("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
