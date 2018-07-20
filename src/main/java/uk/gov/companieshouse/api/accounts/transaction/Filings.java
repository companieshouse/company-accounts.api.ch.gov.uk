package uk.gov.companieshouse.api.accounts.transaction;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Filings {
    private String id;
    @Field("processed_at")
    @JsonProperty("processed_at")
    private Date processedAt;

    private String status;

    private String type;

    @Field("reject_reasons")
    @JsonProperty("reject_reasons")
    private Set<RejectReasons> rejectReasons;

    private Map<String,String> links;

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<RejectReasons> getRejectReasons() {
        return rejectReasons;
    }

    public void setRejectReasons(Set<RejectReasons> rejectReasons) {
        this.rejectReasons = rejectReasons;
    }


}