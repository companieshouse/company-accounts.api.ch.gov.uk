package uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

public class DocumentGeneratorRequest {

    @JsonProperty("resource_uri")
    private String resourceUri;

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
