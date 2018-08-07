package uk.gov.companieshouse.api.accounts.util;

public enum AccountsUtility {

  SUCCESS_MSG("Successfully Completed"),
  FAILURE_MSG("Failed"),
  END_OF_REQUEST_MSG("End of Request %s %s"),
  START_OF_RQUEST_MSG("Start Request %s %s"),
  ERROR_MSG("Error Occured"),

  REQUEST_ID("X-Request-Id"),
  ERIC_IDENTITY("Eric-Identity");

  private String msg;

  AccountsUtility(String msg) {
    this.msg = msg;
  }

  public String value() {
    return msg;
  }
}
