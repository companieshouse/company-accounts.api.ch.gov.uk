package uk.gov.companieshouse.api.accounts.util.logging;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class AccountsLoggerImpl implements AccountsLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  private String requestId;

  private String userId;

  private String transactionId;

  private String accountsId;

  public AccountsLoggerImpl(String requestId, String userId, String transactionId, String accountsId) {
    this.requestId = requestId;
    this.userId = userId;
    this.transactionId = transactionId;
    this.accountsId = accountsId;
  }

  public void logStartOfRequestProcessing(String message) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, "message", message);
    putValue(logData, "identity", userId);
    LOGGER.infoContext(requestId, "", logData);
  }

  public void logEndOfRequestProcessing(String message, int statusCode, final long responseTime) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, "message", message);
    putValue(logData, "status_code", statusCode);
    putValue(logData, "response_time", responseTime);
    LOGGER.infoContext(requestId, "", logData);
  }

  public void logError(String message, Exception exception, int statusCode) {
    Map<String, Object> logData = new HashMap();
    addValues(logData);
    putValue(logData, "message", message);
    putValue(logData, "status_code", statusCode);
    LOGGER.errorContext(requestId, exception, logData);
  }

  private void putValue(Map map, String key, Object value) {
    if (value != null) {
      map.put(key, value);
    }
  }

  private void addValues(Map logData) {
    putValue(logData, "transaction_id", transactionId);
    putValue(logData, "accounts_id", accountsId);
  }
}
