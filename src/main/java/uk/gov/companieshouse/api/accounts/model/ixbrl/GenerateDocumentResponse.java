package uk.gov.companieshouse.api.accounts.model.ixbrl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import java.util.Map;
import org.springframework.hateoas.Links;

public class GenerateDocumentResponse {

    @JsonProperty("links")
    private Links links;

    @JsonProperty("size")
    private String size;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description_values")
    private Map<String, String> descriptionValues;

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
