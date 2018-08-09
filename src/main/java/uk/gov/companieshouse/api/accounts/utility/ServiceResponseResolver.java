package uk.gov.companieshouse.api.accounts.utility;

import static uk.gov.companieshouse.api.accounts.service.response.ResponseStatus.SUCCESS;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;

@Component
public class ServiceResponseResolver {

    public ResponseEntity resolve(ResponseObject responseObject) {
        switch(responseObject.getStatus()) {
            case SUCCESS:
                return ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
            case DUPLICATE_KEY_ERROR:
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
