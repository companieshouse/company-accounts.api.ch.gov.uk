package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.offbalancesheetarrangements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OffBalanceSheetArrangements extends Note {

    @JsonProperty("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OffBalanceSheetArrangements that = (OffBalanceSheetArrangements) o;
        return Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details);
    }

    @Override
    public String toString() {
        return "OffBalanceSheetArrangements{" +
                "details='" + details + '\'' +
                '}';
    }
}