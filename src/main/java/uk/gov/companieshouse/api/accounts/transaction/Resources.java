package uk.gov.companieshouse.api.accounts.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

public class Resources {

    @Field("updated_by")
    @JsonProperty("updated_by")
    private Map<String, String> updatedBy;

    private String kind;

    @Field("updated_at")
    @JsonProperty("updated_at")
    private Date updatedAt;

    private Map<String,String> links;

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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Map<String, String> getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Map<String, String> updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Resources [updatedBy=" + updatedBy + ", kind="
                + kind + ", updatedAt=" + updatedAt + ", links=" + links
                + "]";
    }


}