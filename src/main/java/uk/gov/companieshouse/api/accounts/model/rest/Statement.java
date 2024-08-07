package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotNull;

@JsonInclude(Include.NON_NULL)
public class Statement extends RestObject {

    @NotNull
    @JsonProperty("has_agreed_to_legal_statements")
    private Boolean hasAgreedToLegalStatements;

    @JsonProperty("legal_statements")
    private Map<String, String> legalStatements = new HashMap<>();

    public Boolean getHasAgreedToLegalStatements() {
        return hasAgreedToLegalStatements;
    }

    public void setHasAgreedToLegalStatements(Boolean hasAgreedToLegalStatements) {
        this.hasAgreedToLegalStatements = hasAgreedToLegalStatements;
    }

    public Map<String, String> getLegalStatements() {
        return legalStatements;
    }

    public void setLegalStatements(Map<String, String> legalStatements) {
        this.legalStatements = legalStatements;
    }
}