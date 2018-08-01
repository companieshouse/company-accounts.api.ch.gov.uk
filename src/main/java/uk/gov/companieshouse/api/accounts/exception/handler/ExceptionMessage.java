package uk.gov.companieshouse.api.accounts.exception.handler;

import java.io.IOException;
import org.springframework.dao.DataAccessException;

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

  public static ExceptionMessage getExceptionMessage(Exception ex) {
    if (ex instanceof DataAccessException) {
      return ExceptionMessage.DATA_ACCESS_EXCEPTION;
    } else if (ex instanceof IOException) {
      return ExceptionMessage.IO_EXCEPTION;
    } else if (ex instanceof IllegalArgumentException) {
      return ExceptionMessage.ILLEGAL_ARGUMENT_EXCEPTION;
    } else if (ex instanceof IllegalStateException) {
      return ExceptionMessage.ILLEGAL_STATE_EXCEPTION;
    } else if (ex instanceof NullPointerException) {
      return ExceptionMessage.NULL_POINTER_EXCEPTION;
    } else if (ex instanceof RuntimeException) {
      return ExceptionMessage.RUN_TIME_EXCEPTION;
    }
    return ExceptionMessage.EXCEPTION;
  }
}
