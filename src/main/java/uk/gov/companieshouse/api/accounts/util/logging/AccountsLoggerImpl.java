package uk.gov.companieshouse.api.accounts.util.logging;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;
import static uk.gov.companieshouse.api.accounts.util.AccountsUtility.END_OF_REQUEST_MSG;
import static uk.gov.companieshouse.api.accounts.util.AccountsUtility.START_OF_RQUEST_MSG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class AccountsLoggerImpl implements AccountsLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
  private static final String LOG_MSG_KEY = "message";
  private static final String STATUS_CODE_KEY = "status_code";

  private String requestPath;
  private String requestMethod;
  private String requestId;
  private String userId;


  public AccountsLoggerImpl(String requestPath, String requestMethod, String requestId,
      String userId) {
    this.requestPath = requestPath;
    this.requestMethod = requestMethod;
    this.requestId = requestId;
    this.userId = userId;
  }

  public void logStartOfRequestProcessing() {
    String startMessage = String.format(START_OF_RQUEST_MSG.value(), action(), resource());
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, startMessage);
    LOGGER.infoContext(requestPath, "", logData);
  }

  public void logEndOfRequestProcessing(int statusCode, final long responseTime) {
    String endMessage = String.format(END_OF_REQUEST_MSG.value(), action(), resource());
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, endMessage);
    putValue(logData, STATUS_CODE_KEY, statusCode);
    putValue(logData, "response_time", responseTime);
    LOGGER.infoContext(requestPath, requestId, logData);
  }

  public void logError(String message, Exception exception, int statusCode,
      final long responseTime) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, LOG_MSG_KEY, message);
    putValue(logData, STATUS_CODE_KEY, statusCode);
    putValue(logData, "response_time", responseTime);
    LOGGER.errorContext(requestPath, requestId, exception, logData);
  }

  private void putValue(Map map, String key, Object value) {
    if (value != null) {
      map.put(key, value);
    }
  }

  private void addValues(Map logData) {
    putValue(logData, "identity", userId);
    putValue(logData, "request-id", requestId);
    putValue(logData, "method", requestMethod);
  }

  private String resource(){
    String []temp = requestPath.split("/");
    String action = temp[temp.length -1];
    return action;
  }

  private String action() {
    switch (requestMethod) {
      case "POST":
        return "Create";
      case "GET":
        return "Get";
      case "PUT":
        return "Update";
      case "DELETE":
        return "Delete";
      default:
        return "";
    }
  }
}
