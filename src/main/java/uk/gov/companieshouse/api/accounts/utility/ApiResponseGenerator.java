package uk.gov.companieshouse.api.accounts.utility;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.service.response.ErrorData;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

@Component
public class ApiResponseGenerator {

    public ResponseEntity generateApiResponse(ResponseStatus responseStatus, Object data,
        ErrorData errorData) {
        switch (responseStatus) {
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
