package uk.gov.companieshouse.api.accounts.controller;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseGenerator;

@RestController
public class CurrentPeriodController {

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Autowired
    private ApiResponseGenerator exceptionHandler;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountsId}/small-full/current-period",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody CurrentPeriod currentPeriod,
            HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getSession()
                .getAttribute(AttributeName.TRANSACTION.getValue());

        CurrentPeriod result = null;
        try {
            result = currentPeriodService
                    .save(currentPeriod, transaction.getCompanyNumber());
        } catch (DuplicateKeyException dke) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (NoSuchAlgorithmException | MongoException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(result);

    }
}
