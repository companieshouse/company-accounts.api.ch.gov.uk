package uk.gov.companieshouse.api.accounts.model.rest.smallfull.fixedassetsinvestments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FixedAssetsInvestments extends Note {

    @Valid
    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public int hashCode() {
        return Objects.hash(details);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FixedAssetsInvestments)) {
            return false;
        }
        FixedAssetsInvestments other = (FixedAssetsInvestments) obj;
        return Objects.equals(details, other.details);
    }

    @Override
    public String toString() {
        return "FixedAssetsInvestments [details=" + details + "]";
    }
}