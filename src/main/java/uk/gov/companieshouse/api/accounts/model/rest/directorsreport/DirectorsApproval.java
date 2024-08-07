package uk.gov.companieshouse.api.accounts.model.rest.directorsreport;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.PastOrPresent;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.validation.AfterCurrentPeriod;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class DirectorsApproval extends RestObject {

    private static final int MAX_FIELD_LENGTH = 120;
    private static final int MIN_FIELD_LENGTH = 1;

    @NotBlank
    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @JsonProperty("name")
    private String name;

    @NotNull
    @PastOrPresent
    @AfterCurrentPeriod(accountType = AccountType.SMALL_FULL)
    @JsonProperty("date")
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
