package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.currentassetsinvestments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentAssetsInvestments extends Note {

    private static final int MAX_FIELD_LENGTH = 20000;
    private static final int MIN_FIELD_LENGTH = 1;

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
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof CurrentAssetsInvestments)) {return false;}
        CurrentAssetsInvestments that = (CurrentAssetsInvestments) o;
        return Objects.equals(getDetails(), that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDetails());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
