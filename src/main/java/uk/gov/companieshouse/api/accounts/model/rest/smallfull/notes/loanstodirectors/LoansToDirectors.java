package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(Include.NON_NULL)
public class LoansToDirectors extends RestObject {

    @JsonProperty("loans")
    private Map<String, String> loans;

    public Map<String, String> getLoans() {
        return loans;
    }

    public void setLoans(Map<String, String> loans) {
        this.loans = loans;
    }
}
