package uk.gov.companieshouse.api.accounts.utility;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.service.response.ValidationErrorData;
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
            ValidationErrorData validationErrorData) {
        switch (status) {
            case CREATED:
                return ResponseEntity.status(HttpStatus.CREATED).body(restObject);
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity map(Exception exception) {
        if (exception instanceof DataException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else if (exception instanceof PatchException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
