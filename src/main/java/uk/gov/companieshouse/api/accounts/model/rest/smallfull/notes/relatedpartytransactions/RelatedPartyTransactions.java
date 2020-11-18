package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

import java.util.Map;

@JsonInclude(Include.NON_NULL)
public class RelatedPartyTransactions extends RestObject {

    @JsonProperty("transactions")
    private Map<String, String> transactions;

    public Map<String, String> getTransactions() {
        return transactions;
    }

    public void setTransactions(Map<String, String> transactions) {
        this.transactions = transactions;
    }
}


