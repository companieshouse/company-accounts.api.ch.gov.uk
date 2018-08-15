package uk.gov.companieshouse.api.accounts.util;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.END_OF_REQUEST_MSG;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.LOG_MSG_KEY;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.REQUEST_ID_MSG_KEY;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.REQUEST_METHOD_KEY;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.RESPONSE_TIME_KEY;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.START_OF_REQUEST_MSG;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.STATUS_CODE_KEY;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.USER_ID_KEY;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class AccountsLoggerImpl implements AccountsLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    public void logStartOfRequestProcessing(RequestContext reqCtx) {
        String startMessage = String.format(START_OF_REQUEST_MSG.value());
        Map<String, Object> logData = new HashMap();
        addValues(logData,reqCtx);
        putValue(logData, LOG_MSG_KEY.value(), startMessage);
        LOGGER.infoContext(reqCtx.path(), reqCtx.id(), logData);
    }

    public void logEndOfRequestProcessing(RequestContext reqCtx, int statusCode, final long responseTime) {
        String endMessage = String.format(END_OF_REQUEST_MSG.value());
        Map<String, Object> logData = new HashMap();
        addValues(logData, reqCtx);
        putValue(logData, LOG_MSG_KEY.value(), endMessage);
        putValue(logData, STATUS_CODE_KEY.value(), statusCode);
        putValue(logData, RESPONSE_TIME_KEY.value(), responseTime);
        LOGGER.infoContext(reqCtx.path(), reqCtx.id(), logData);
    }

    public void logError(RequestContext reqCtx, String message, Exception exception, int statusCode,
        final long responseTime) {
        Map<String, Object> logData = new HashMap();
        addValues(logData, reqCtx);
        putValue(logData, LOG_MSG_KEY.value(), message);
        putValue(logData, STATUS_CODE_KEY.value(), statusCode);
        putValue(logData, RESPONSE_TIME_KEY.value(), responseTime);
        LOGGER.errorContext(reqCtx.path(), reqCtx.id(), exception, logData);
    }

    private void putValue(Map map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private void addValues(Map logData, RequestContext reqCtx) {
        putValue(logData, USER_ID_KEY.value(), reqCtx.userId());
        putValue(logData, REQUEST_ID_MSG_KEY.value(), reqCtx.id());
        putValue(logData, REQUEST_METHOD_KEY.value(), reqCtx.method());
    }
}
