package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @ExceptionHandler(value = {NoHandlerFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  protected void handleNoHandlerFoundException(Exception ex) {
    logError(ex, ExceptionMessage.NO_HANDLER_FOUND_EXCEPTION);
  }

  @ExceptionHandler(value = {DataAccessException.class, IOException.class,
      IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class,
      NoSuchAlgorithmException.class, RuntimeException.class, Exception.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleException(Exception ex) {
    logError(ex, getExceptionMessage(ex));
  }

  private void logError(Exception ex, ExceptionMessage exceptionMessage) {
    HashMap<String, Object> message = new HashMap<>();
    message.put("message", ex.getMessage());
    message.put("error", exceptionMessage.getMessage());
    LOGGER.error(exceptionMessage.getError(), ex, message);
  }

  private ExceptionMessage getExceptionMessage(Exception ex) {
    return ExceptionMessage.getExceptionMessage(ex);
  }
}
