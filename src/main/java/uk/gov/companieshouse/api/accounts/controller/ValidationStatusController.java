package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.model.validation.ValidationStatus;
import uk.gov.companieshouse.api.accounts.service.ValidationStatusService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/validate", produces = MediaType.APPLICATION_JSON_VALUE)
public class ValidationStatusController {

    @Autowired
    private ValidationStatusService validationStatusService;

    @GetMapping
    public ResponseEntity<ValidationStatus> getValidationStatus(@PathVariable("companyAccountId") String companyAccountId,
                                                                HttpServletRequest request ) {

        try {
            Errors errors = validationStatusService.getValidationErrors(companyAccountId, request);

            ValidationStatus validationStatus = new ValidationStatus(!errors.hasErrors(), errors.getErrors());

            return ResponseEntity.ok(validationStatus);
        } catch (DataException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
