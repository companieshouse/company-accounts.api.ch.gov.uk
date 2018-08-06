package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


/**
 * GlobalExceptionHandler defines handlers for generic exceptions.
 *
 * Api Specific Errors are handled in the Controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

  @ExceptionHandler(value = {NoHandlerFoundException.class})
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  protected void handleNoHandlerFoundException(Exception ex) {
    logError(ex);
  }

  @ExceptionHandler(value = {RuntimeException.class, Exception.class})
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  protected void handleException(Exception ex) {
    logError(ex);
  }

  private void logError(Exception ex) {
    HashMap<String, Object> message = new HashMap<>();
    message.put("message", ex.getMessage());
    message.put("error", ex.getClass());
    LOGGER.error(ex, message);
  }
}
