package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanBreakdownResourceEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends RestObject {

    @JsonProperty("director_name")
    private String directorName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("breakdown")
    private LoanBreakdownResourceEntity breakdown;

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

    public LoanBreakdownResourceEntity getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(LoanBreakdownResourceEntity breakdown) {
        this.breakdown = breakdown;
    }
}
