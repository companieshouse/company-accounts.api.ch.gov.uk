package uk.gov.companieshouse.api.accounts.model.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Link {

    @JsonProperty("rel")
    private String relationship;

    @JsonProperty("href")
    private String link;

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
