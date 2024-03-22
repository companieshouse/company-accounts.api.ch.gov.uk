package uk.gov.companieshouse.api.accounts.utility;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;

@Component
public class ApiResponseMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    /**
     * Builds a Response Entity based on the supplied status, entity and error data.
     */
    public ResponseEntity map(ResponseStatus status, RestObject restObject, Errors errors) {
        return switch (status) {
            case CREATED -> ResponseEntity.status(HttpStatus.CREATED).body(restObject);
            case UPDATED -> ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            case DUPLICATE_KEY_ERROR -> ResponseEntity.status(HttpStatus.CONFLICT).build();
            case VALIDATION_ERROR -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        };
    }

    public ResponseEntity getErrorResponse() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public ResponseEntity mapGetResponse(RestObject restObject, HttpServletRequest request) {
        if (restObject == null) {
            LogContext logContext = LogHelper.createNewLogContext(request);
            LOGGER.debugLogContext("Resource not found", logContext);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(restObject);
    }

    public ResponseEntity mapGetResponseForMultipleResources(RestObject[] restObjects, HttpServletRequest request) {
        if (restObjects == null) {
            LogContext logContext = LogHelper.createNewLogContext(request);
            LOGGER.debugLogContext("Resource not found", logContext);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(restObjects);
    }
}
