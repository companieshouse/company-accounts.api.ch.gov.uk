package uk.gov.companieshouse.api.accounts.controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@RestController
public class FilingController {

    @Autowired
    private FilingService filingService;

    @GetMapping("/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings")
    public ResponseEntity generateFiling(@PathParam("transactionId") String transactionId,
        @PathParam("accountId") String accountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getSession()
            .getAttribute(AttributeName.TRANSACTION.getValue());

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request.getSession()
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        try {
            Filing filing = filingService.generateAccountFiling(transaction, companyAccountEntity);
            if (filing != null) {
                return new ResponseEntity<>(Arrays.asList(filing), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}