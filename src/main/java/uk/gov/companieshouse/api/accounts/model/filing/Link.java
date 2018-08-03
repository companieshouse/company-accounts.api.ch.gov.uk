package uk.gov.companieshouse.api.accounts.model.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {

    @JsonProperty("rel")
    private String relationship;

    @JsonProperty("href")
    private String href;

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
