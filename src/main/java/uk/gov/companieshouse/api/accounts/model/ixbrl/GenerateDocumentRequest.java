package uk.gov.companieshouse.api.accounts.model.ixbrl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;


public class GenerateDocumentRequest {

    @JsonProperty("resource_uri")
    private String resourceUri;

    @JsonProperty("resource_id")
    private String resourceID;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("document_type")
    private String documentType;

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
