package uk.gov.companieshouse.api.accounts.util.ixbrl;

public class DocumentGeneratorConnection {
    private String requestMethod;
    private String serviceURL;
    private String requestBody;
    private String authorizationProperty;
    private String assetId;
    private String contentType;
    private String acceptType;
    private String location;
    private String templateName;
    private boolean setDoOutPut;

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getAuthorizationProperty() {
        return authorizationProperty;
    }

    public void setAuthorizationProperty(String authorizationProperty) {
        this.authorizationProperty = authorizationProperty;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getAcceptType() {
        return acceptType;
    }

    public void setAcceptType(String acceptType) {
        this.acceptType = acceptType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isSetDoOutPut() {
        return setDoOutPut;
    }

    public void setSetDoOutPut(boolean setDoOutPut) {
        this.setDoOutPut = setDoOutPut;
    }
}
