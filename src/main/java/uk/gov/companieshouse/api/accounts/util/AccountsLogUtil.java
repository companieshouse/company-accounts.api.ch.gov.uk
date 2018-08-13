package uk.gov.companieshouse.api.accounts.util;

public enum AccountsLogUtil {
    END_OF_REQUEST_MSG("End of Request %s %s"),
    START_OF_RQUEST_MSG("Start Request %s %s"),

    REQUEST_ID("X-Request-Id"),
    ERIC_IDENTITY("Eric-Identity");

    private String msg;

    AccountsLogUtil(String msg) {
        this.msg = msg;
    }

    public String value() {
        return msg;
    }
}
