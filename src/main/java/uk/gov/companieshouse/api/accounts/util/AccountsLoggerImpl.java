package uk.gov.companieshouse.api.accounts.util;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.END_OF_REQUEST_MSG;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.START_OF_RQUEST_MSG;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class AccountsLoggerImpl implements AccountsLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    private static final String LOG_MSG_KEY = "message";
    private static final String STATUS_CODE_KEY = "status_code";

    public void logStartOfRequestProcessing(RequestContext reqCtx) {
        String startMessage = String.format(START_OF_RQUEST_MSG.value());
        Map<String, Object> logData = new HashMap();
        addValues(logData,reqCtx);
        putValue(logData, LOG_MSG_KEY, startMessage);
        LOGGER.infoContext(reqCtx.path(), "", logData);
    }

    public void logEndOfRequestProcessing(RequestContext reqCtx, int statusCode, final long responseTime) {
        String endMessage = String.format(END_OF_REQUEST_MSG.value());
        Map<String, Object> logData = new HashMap();
        addValues(logData, reqCtx);
        putValue(logData, LOG_MSG_KEY, endMessage);
        putValue(logData, STATUS_CODE_KEY, statusCode);
        putValue(logData, "response_time", responseTime);
        LOGGER.infoContext(reqCtx.path(), reqCtx.id(), logData);
    }

    public void logError(RequestContext reqCtx, String message, Exception exception, int statusCode,
        final long responseTime) {
        Map<String, Object> logData = new HashMap();
        addValues(logData, reqCtx);
        putValue(logData, LOG_MSG_KEY, message);
        putValue(logData, STATUS_CODE_KEY, statusCode);
        putValue(logData, "response_time", responseTime);
        LOGGER.errorContext(reqCtx.path(), reqCtx.id(), exception, logData);
    }

    private void putValue(Map map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private void addValues(Map logData, RequestContext reqCtx) {
        putValue(logData, "identity", reqCtx.userId());
        putValue(logData, "request-id", reqCtx.id());
        putValue(logData, "method", reqCtx.method());
    }
}
