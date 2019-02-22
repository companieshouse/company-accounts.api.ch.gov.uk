package uk.gov.companieshouse.api.accounts.utility;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
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
    public ResponseEntity map(ResponseStatus status, RestObject restObject,
        Errors errors) {
        switch (status) {
            case CREATED:
                return ResponseEntity.status(HttpStatus.CREATED).body(restObject);
            case UPDATED:
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            case VALIDATION_ERROR:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
            case NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
}
