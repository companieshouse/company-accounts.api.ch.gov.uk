package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Loan extends RestObject {

    private static final int MIN_FIELD_LENGTH = 1;
    private static final int DIRECTORS_NAME_MAX_FIELD_LENGTH = 120;
    private static final int DESCRIPTION_MAX_FIELD_LENGTH = 250;

    @CharSetValid(CharSet.CHARACTER_SET_2)
    @Size(min = MIN_FIELD_LENGTH, max = DIRECTORS_NAME_MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("director_name")
    private String directorName;

    @CharSetValid(CharSet.CHARACTER_SET_3)
    @Size(min = MIN_FIELD_LENGTH, max = DESCRIPTION_MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("description")
    private String description;

    @Valid
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
