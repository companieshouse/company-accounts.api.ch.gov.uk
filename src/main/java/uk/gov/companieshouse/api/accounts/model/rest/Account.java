package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;

public class Account {

    @NotNull
    @JsonProperty("period_end_on")
    private LocalDate periodEndOn;

    @JsonProperty("links")
    private Map<String, String> links = new HashMap<>();

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("etag")
    private String etag;

    public LocalDate getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(LocalDate periodEndOn) {
        this.periodEndOn = periodEndOn;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
