package uk.gov.companieshouse.api.accounts.exceptionhandler;

import java.security.NoSuchAlgorithmException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NoSuchAlgorithmException.class)
    protected ResponseEntity<Object> handleConflict(NoSuchAlgorithmException ex, WebRequest
            request) {
        String bodyOfResponse = "An internal exception has occurred";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
