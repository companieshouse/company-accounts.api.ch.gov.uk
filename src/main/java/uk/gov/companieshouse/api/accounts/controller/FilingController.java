package uk.gov.companieshouse.api.accounts.controller;

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class FilingController {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @GetMapping("/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings")
    public ResponseEntity generateFiling() {

        return new ResponseEntity<>(Arrays.asList(new Filing()), HttpStatus.OK);
    }
}