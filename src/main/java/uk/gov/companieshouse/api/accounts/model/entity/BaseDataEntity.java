package uk.gov.companieshouse.api.accounts.model.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseDataEntity implements Serializable {

    private Map<String, String> links = new HashMap<>();

    private String etag;

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
