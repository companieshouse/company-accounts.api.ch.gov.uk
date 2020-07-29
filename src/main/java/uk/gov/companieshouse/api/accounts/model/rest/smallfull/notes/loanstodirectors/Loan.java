package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends RestObject {

    @JsonProperty("director_name")
    private String directorName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("breakdown")
    private LoanBreakdownResource breakdown;

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LoanBreakdownResource getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(LoanBreakdownResource breakdown) {
        this.breakdown = breakdown;
    }
}
