package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.service.response.ValidationErrorData;

@Component
public class ApiResponseMapper {

    /**
     * Builds a Response Entity based on the supplied
     * status, entity and error data.
     * @param restObject
     * @return
     */
    public ResponseEntity map(ResponseStatus status, RestObject restObject,
            ValidationErrorData validationErrorData) {
        switch (status) {
            case SUCCESS_CREATED:
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
}
