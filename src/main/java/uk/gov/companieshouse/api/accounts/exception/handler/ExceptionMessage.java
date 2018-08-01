package uk.gov.companieshouse.api.accounts.exception.handler;

public enum ExceptionMessage {
  NO_HANDLER_FOUND_EXCEPTION("No Handler Found for the requested resouce",
      "NoHandlerFoundException"),
  DATA_ACCESS_EXCEPTION("DataAccessException from Accounts API", "Data Access Exception"),
  IO_EXCEPTION("IOException from Accounts API", "IOException"),
  ILLEGAL_ARGUMENT_EXCEPTION("Illegal Argument Exception from Accounts API",
      " Illegal Argument Exception"),
  ILLEGAL_STATE_EXCEPTION("IllegalStateException from Accounts API", "IllegalStateException"),
  NULL_POINTER_EXCEPTION("NullPointerException from Accounts API", "NullPointerException"),
  RUN_TIME_EXCEPTION("RunException from Accounts API", "RuntimeException"),
  EXCEPTION("Exception from Accounts API", "Exception"),

  TRANSACTION_NOT_FOUND("Transaction not Found", "Accounts API Error"),
  TRANSACTION_STATUS_NOT_OPEN("Cannot Update on a Transaction with Closed status",
      "Accounts API Error"),
  ACCOUNT_NOT_FOUND("Account not Found", "Accounts API Error"),
  DUPLICATE_PERIOD_END_ON_DATE("Duplicate Period End On Date", "Accounts API Error"),
  INVALID_ACCOUNT_TYPE("Account Type is Invalid", "Accounts API Error"),
  ACCOUNT_LINK_PRESENT("Account Link Already Present", "Accounts API Error"),
  ACCOUNT_TYPE_PRESENT("Account Type Already Present", "Accounts API Error"),
  ACCOUNT_TYPE_LINK_ABSENT("Account Type Link Absent", "Accounts API Error"),
  INTERNAL_ERROR("Internal Server Error", "Accounts API Error");

  private String message;
  private String error;

  ExceptionMessage(String message, String error) {
    this.message = message;
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public String getError() {
    return error;
  }
}
