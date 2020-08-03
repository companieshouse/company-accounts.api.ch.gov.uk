package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalInformation extends RestObject {

    @JsonProperty("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
