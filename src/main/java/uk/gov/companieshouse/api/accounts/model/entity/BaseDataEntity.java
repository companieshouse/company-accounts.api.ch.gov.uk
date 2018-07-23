package uk.gov.companieshouse.api.accounts.model.entity;

import java.util.HashMap;
import java.util.Map;

public class BaseDataEntity {

    private Map<String, String> links = new HashMap<>();

    private String etag;

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

}
