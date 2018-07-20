package uk.gov.companieshouse.api.accounts.transaction;

import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Transaction {

    private String id;

    @JsonProperty("closed_at")
    private Date closedAt;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("created_at")
    private Date createdAt;

    private String kind;

    private String reference;

    private String status;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty("created_by")
    private Map<String,String> createdBy;

    private Map<String,String> links;

    private Map<String, Filings> filings;

    public Map<String, Filings> getFilings() {
        return filings;
    }

    public void setFilings(Map<String, Filings> filings) {
        this.filings = filings;
    }

    private String etag;

    private Map<String,Resources> resources;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Date closedAt) {
        this.closedAt = closedAt;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, String> getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Map<String, String> createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Map<String, Resources> getResources() {
        return resources;
    }

    public void setResources(Map<String, Resources> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", closedAt=" + closedAt + ", companyNumber=" + companyNumber + ", createdAt="
                + createdAt + ", kind=" + kind + ", reference=" + reference + ", status=" + status + ", updatedAt="
                + updatedAt + ", createdBy=" + createdBy + ", links=" + links + ", filings=" + filings + ", etag="
                + etag + ", resources=" + resources + "]";
    }

}