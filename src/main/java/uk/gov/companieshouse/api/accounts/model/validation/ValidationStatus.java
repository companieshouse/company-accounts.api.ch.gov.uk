package uk.gov.companieshouse.api.accounts.model.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class ValidationStatus {

    public ValidationStatus(boolean isValid, Set<Error> errors) {
        this.isValid = isValid;
        this.errors = errors;
    }

    @JsonProperty("is_valid")
    private boolean isValid;

    @JsonProperty("errors")
    private Set<Error> errors;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public Set<Error> getErrors() {
        return errors;
    }

    public void setErrors(Set<Error> errors) {
        this.errors = errors;
    }
}
