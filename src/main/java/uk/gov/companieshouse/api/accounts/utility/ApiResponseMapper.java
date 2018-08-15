package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.service.response.ErrorData;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

@Component
public class ApiResponseMapper {

    /**
     * Builds a Response Entity based on the supplied
     * status, entity and error data.
     * @param object
     * @return
     */
    public ResponseEntity map(ResponseStatus status, Object object, ErrorData errorData) {
        switch (status) {
            case SUCCESS_CREATED:
                return ResponseEntity.status(HttpStatus.CREATED).body(object);
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
