package uk.gov.companieshouse.api.accounts.util;

public interface AccountsLogger {

    void logStartOfRequestProcessing(RequestContext requestContext);

    void logEndOfRequestProcessing(RequestContext requestContext, int statusCode, long responseTime);

    void logError(RequestContext requestContext, String message, Exception exception, int statusCode, long responseTime);
}
