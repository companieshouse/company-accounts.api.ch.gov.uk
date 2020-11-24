package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class Costs {

    @JsonProperty("costs")
    private Map<String, Cost> costsMap;

    public Map<String, Cost> getCostsList() {
        return costsMap;
    }

    public void setCostsList(
            Map<String, Cost> costs) {
        this.costsMap = costs;
    }
}
