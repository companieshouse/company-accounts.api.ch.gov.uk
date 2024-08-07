package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.financialcommitments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialCommitments extends Note {

    @NotNull
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialCommitments that = (FinancialCommitments) o;
        return Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(details);
    }

    @Override
    public String toString() {
        return "FinancialCommitments{" +
                "details='" + details + '\'' +
                '}';
    }
}