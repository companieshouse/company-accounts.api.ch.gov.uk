package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

@Component
public class ApiResponseGenerator {

    /**
     * Builds a Response Entity based on the
     * status, entity and error data encapsulated in the ResponseObject.
     * @param responseObject - response received from the service methods
     * @return
     */
    public ResponseEntity getApiResponse(ResponseObject responseObject) {
        switch (responseObject.getStatus()) {
            case SUCCESS_CREATED:
                return ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
