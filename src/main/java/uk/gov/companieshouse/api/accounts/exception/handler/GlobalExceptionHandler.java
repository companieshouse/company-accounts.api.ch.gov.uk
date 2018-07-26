package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.io.IOException;
import java.util.HashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @ExceptionHandler(value = { DataAccessException.class, IOException.class,
      IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class, RuntimeException.class, Exception.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleException(Exception ex) {
    getExceptionMap(ex, getExceptionMessage(ex));
  }

  @ExceptionHandler(value = { NoHandlerFoundException.class })
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  protected void handleNoHandlerFoundException(Exception ex) {
    getExceptionMap(ex,  ExceptionMessage.NO_HANDLER_FOUND_EXCEPTION);
  }


  private void getExceptionMap(Exception ex, ExceptionMessage exceptionMessage) {
    HashMap<String, Object> message = new HashMap<>();
    message.put("message", ex.getMessage());
    message.put("error", exceptionMessage.getMessage());
    LOGGER.error(exceptionMessage.getMessage(), message);
  }

  private ExceptionMessage getExceptionMessage(Exception ex) {
    if (ex instanceof DataAccessException) {
      return ExceptionMessage.DATA_ACCESS_EXCEPTION;
    }
    if (ex instanceof IOException) {
      return ExceptionMessage.IO_EXCEPTION;
    }
    if (ex instanceof IllegalArgumentException) {
      return ExceptionMessage.ILLEGAL_ARGUMENT_EXCEPTION;
    }
    if (ex instanceof IllegalStateException) {
      return ExceptionMessage.ILLEGAL_STATE_EXCEPTION;
    }
    if (ex instanceof NullPointerException) {
      return ExceptionMessage.NULL_POINTER_EXCEPTION;
    }
    if (ex instanceof RuntimeException) {
      return ExceptionMessage.RUN_TIME_EXCEPTION;
    }
    return ExceptionMessage.EXCEPTION;
  }

}
