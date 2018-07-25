package uk.gov.companieshouse.api.accounts.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;

@RestController
public class SmallFullController {

    @Autowired
    private SmallFullService smallFullService;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody SmallFull smallFull)
            throws NoSuchAlgorithmException {
        //TODO accountNumber should be retrieved from the Transaction after it becomes available via the interceptor
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[6];
        random.nextBytes(bytes);
        String accountNumber = new String(bytes);

        SmallFull result = smallFullService.save(smallFull, accountNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
