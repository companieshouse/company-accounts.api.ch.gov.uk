package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.HashMap;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorMessageKeys;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
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
        logClientError(ex);
        return super.handleNoHandlerFoundException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
        WebRequest request) {
        logClientError(ex);

        StringBuilder message = new StringBuilder("JSON parse exception");
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            message.append(":Can not deserialize value of ").append(ife.getValue())
                .append(getLocationMessage(ife.getLocation()));
        } else if (cause instanceof JsonProcessingException) {
          JsonProcessingException jpe = (JsonProcessingException) cause;
          message.append(getLocationMessage(jpe.getLocation()));
        }

        Errors errors = new Errors();
        Error error = new Error(ErrorMessageKeys.INVALID_VALUE.getKey(), message.toString(),
            LocationType.JSON_BODY.getValue(), ErrorType.VALIDATION.getType());
        errors.addError(error);
        return new ResponseEntity<>(errors, headers, status);
    }

    private String getLocationMessage(JsonLocation jsonLocation) {
        return " at line " + jsonLocation.getLineNr() + " column " + jsonLocation
            .getColumnNr();
    }

    private void logClientError(Exception ex) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("message", ex.getMessage());
        message.put("error", ex.getClass());
        STRUCTURED_LOGGER.info(ex.getMessage(), message);
    }
}
