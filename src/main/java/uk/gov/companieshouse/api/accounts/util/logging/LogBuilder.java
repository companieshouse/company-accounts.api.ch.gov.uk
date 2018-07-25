package uk.gov.companieshouse.api.accounts.util.logging;

/**
 * Builds logs for
 */
public interface LogBuilder {

  void logStartOfRequestProcessing(String message);

  void logEndOfRequestProcessing(String message, int statusCode, long responseTime);

  void logError(String message, Exception exception, int statusCode);
}
