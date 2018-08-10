package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.service.response.ErrorData;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

@Component
public class ApiResponseGenerator {

    /**
     * Builds a Response Entity based on the supplied status, entity and error data.
     * @param responseObject
     * @return
     */
    public ResponseEntity getApiResponse(ResponseObject responseObject) {
        switch (responseObject.getStatus()) {
            case SUCCESS_CREATED:
                return ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
