package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorMessageKeys;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * GlobalExceptionHandler defines handlers for generic exceptions.
 *
 * Api Specific Errors are handled in the Controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger STRUCTURED_LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        logError(ex);
        return super.handleNoHandlerFoundException(ex, headers, status, request);
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
        STRUCTURED_LOGGER.error(ex, message);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {

        HashMap<String, Object> message = new HashMap<>();
        message.put("message", ex.getMessage());
        message.put("error", ex.getClass());
        STRUCTURED_LOGGER.info(ex.getMessage(), message);

        if (ex.getCause() instanceof InvalidFormatException) {
            Errors errors = new Errors();
            Error error = new Error(ErrorMessageKeys.INVALID_VALUE.getKey(), null,
                null, ErrorType.VALIDATION.getType());
            errors.addError(error);
            return new ResponseEntity<>(errors, headers, status);
        } else {
            return this.handleExceptionInternal(ex, null, headers, status, request);
        }
    }

}
