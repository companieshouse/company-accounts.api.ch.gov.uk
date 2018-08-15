package uk.gov.companieshouse.api.accounts.util;

public enum AccountsLogUtil {
    END_OF_REQUEST_MSG("End of Request"),
    START_OF_REQUEST_MSG("Start Request"),
    REQUEST_ID("X-Request-Id"),
    ERIC_IDENTITY("Eric-Identity"),
    START_TIME_KEY("START_TIME"),
    RESPONSE_TIME_KEY("response_time"),
    STATUS_CODE_KEY("status_code"),
    LOG_MSG_KEY("message"),
    USER_ID_KEY("identity"),
    REQUEST_METHOD_KEY("method"),
    REQUEST_ID_MSG_KEY("request-id");

    private String msg;

    AccountsLogUtil(String msg) {
        this.msg = msg;
    }

    public String value() {
        return msg;
    }
}
