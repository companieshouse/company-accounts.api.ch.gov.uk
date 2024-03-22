package uk.gov.companieshouse.api.accounts.exception.handler;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * GlobalExceptionHandler defines handlers for generic exceptions.
 * <p>
 * Api Specific Errors are handled in the Controller.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger STRUCTURED_LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Value("${invalid.value}")
    private String invalidValue;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleException(Exception ex) {
        HashMap<String, Object> message = new HashMap<>();
        message.put("message", ex.getMessage());
        message.put("error", ex.getClass());
        STRUCTURED_LOGGER.error(ex.getMessage(), message);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(@NonNull NoHandlerFoundException ex,
                                                                   @NonNull HttpHeaders headers,
                                                                   @NonNull HttpStatusCode status,
                                                                   @NonNull WebRequest request) {
        logClientError(ex);
        return super.handleNoHandlerFoundException(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        logClientError(ex);

        StringBuilder message = new StringBuilder("JSON parse exception");
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            message.append(":Can not deserialize value of ").append(ife.getValue())
                .append(getLocationMessage(ife.getLocation()));
        } else if (cause instanceof JsonProcessingException jpe) {
          message.append(getLocationMessage(jpe.getLocation()));
        }

        Errors errors = new Errors();
        Error error = new Error(invalidValue, message.toString(),
            LocationType.REQUEST_BODY.getValue(), ErrorType.VALIDATION.getType());
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
