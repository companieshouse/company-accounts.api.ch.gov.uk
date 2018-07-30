package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.accounts.exception.ApiException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @ExceptionHandler(value = {DataAccessException.class, IOException.class,
      IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class,
      RuntimeException.class, NoSuchAlgorithmException.class, Exception.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleException(Exception ex) {
    logError(ex, getExceptionMessage(ex));
  }

  @ExceptionHandler(value = {NoHandlerFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  protected void handleNoHandlerFoundException(Exception ex) {
    logError(ex, ExceptionMessage.NO_HANDLER_FOUND_EXCEPTION);
  }

  @ExceptionHandler(value = {ApiException.class})
  protected void handleApiException(ApiException apiex) {
    logError(apiex, apiex.exceptionMessage);
    throw new ResponseStatusException(getHttpStatus(apiex), apiex.exceptionMessage.getMessage());
  }


  private void logError(Exception ex, ExceptionMessage exceptionMessage) {
    HashMap<String, Object> message = new HashMap<>();
    message.put("message", ex.getMessage());
    message.put("error", exceptionMessage.getError());
    LOGGER.error(exceptionMessage.getMessage(), ex, message);
  }

  private ExceptionMessage getExceptionMessage(Exception ex) {
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

  private HttpStatus getHttpStatus(ApiException apiEx) {
    switch (apiEx.exceptionMessage) {
      case ACCOUNT_LINK_PRESENT:
        return HttpStatus.CONFLICT;
      case ACCOUNT_TYPE_PRESENT:
        return HttpStatus.INTERNAL_SERVER_ERROR;
      case TRANSACTION_STATUS_NOT_OPEN:
      case DUPLICATE_PERIOD_END_ON_DATE:
      case INVALID_ACCOUNT_TYPE:
      case ACCOUNT_TYPE_LINK_ABSENT:
        return HttpStatus.BAD_REQUEST;
      case TRANSACTION_NOT_FOUND:
      case ACCOUNT_NOT_FOUND:
        return HttpStatus.NOT_FOUND;
      case INTERNAL_ERROR:
        return HttpStatus.INTERNAL_SERVER_ERROR;
      default:
    }
    return null;
  }
}
