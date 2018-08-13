package uk.gov.companieshouse.api.accounts.util;

public class RequestContext {

    private final String path;
    private final String method;
    private final String id;
    private final String userId;

    public RequestContext(String path, String method, String id, String userId) {
        this.path = path;
        this.method = method;
        this.id = id;
        this.userId = userId;
    }

    public String path() {
        return path;
    }

    public String method() {
        return method;
    }

    public String id() {
        return id;
    }

    public String userId() {
        return userId;
    }
}
