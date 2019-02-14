package uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

import javax.validation.Valid;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FixedAssetsInvestments extends RestObject {

    @Valid
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