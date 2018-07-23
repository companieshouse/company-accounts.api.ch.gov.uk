package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

public class RestObject {

    @JsonProperty("links")
    private Map<String, String> links = new HashMap<>();

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
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

}
