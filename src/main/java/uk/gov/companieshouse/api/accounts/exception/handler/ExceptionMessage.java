package uk.gov.companieshouse.api.accounts.exception.handler;

public enum ExceptionMessage {
  NO_HANDLER_FOUND_EXCEPTION("No Handler Defined for this path", "Requested Resource Not Found"),
  DATA_ACCESS_EXCEPTION("DataAccessException from Accounts API", "Data Access Exception"),
  IO_EXCEPTION("IOException from Accounts API", "IOException"),
  ILLEGAL_ARGUMENT_EXCEPTION("Illegal Argument Exception from Accounts API",
      " Illegal Argument Exception"),
  ILLEGAL_STATE_EXCEPTION("IllegalStateException from Accounts API", "IllegalStateException"),
  NULL_POINTER_EXCEPTION("NullPointerException from Accounts API", "NullPointerException"),
  RUN_TIME_EXCEPTION("RunException from Accounts API", "RuntimeException"),
  EXCEPTION("Exception from Accounts API", "Exception");

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
