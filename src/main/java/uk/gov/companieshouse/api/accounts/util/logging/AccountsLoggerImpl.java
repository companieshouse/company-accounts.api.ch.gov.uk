package uk.gov.companieshouse.api.accounts.util.logging;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class AccountsLoggerImpl implements AccountsLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
  private static final String LOG_MSG_KEY = "message";
  private static final String STATUS_CODE_KEY = "status_code";

  private String requestId;

  private String userId;

  private List<String> resourceIds;

  public AccountsLoggerImpl(String requestId, String userId, List<String> resourceIds) {
    this.requestId = requestId;
    this.userId = userId;
    this.resourceIds = resourceIds;
  }

  public void logStartOfRequestProcessing(String message) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, message);
    putValue(logData, "identity", userId);
    LOGGER.infoContext(requestId, "", logData);
  }

  public void logEndOfRequestProcessing(String message, int statusCode, final long responseTime) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, message);
    putValue(logData, STATUS_CODE_KEY, statusCode);
    putValue(logData, "response_time", responseTime);
    LOGGER.infoContext(requestId, "", logData);
  }

  public void logError(String message, Exception exception, int statusCode) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, message);
    putValue(logData, STATUS_CODE_KEY, statusCode);
    LOGGER.errorContext(requestId, exception, logData);
  }

  private void putValue(Map map, String key, Object value) {
    if (value != null) {
      map.put(key, value);
    }
  }

  private void addValues(Map logData) {
    putValue(logData, "resources", resourceIds);
  }
}
