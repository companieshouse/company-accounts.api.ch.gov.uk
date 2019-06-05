package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Costs {

    @JsonProperty("costs")
    private Map<String, Cost> costs;

    public Map<String, Cost> getCosts() {
        return costs;
    }

    public void setCosts(
            Map<String, Cost> costs) {
        this.costs = costs;
    }
}
