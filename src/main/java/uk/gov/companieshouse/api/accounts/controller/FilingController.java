package uk.gov.companieshouse.api.accounts.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class FilingController {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String FILING_CONTROLLER_ERROR = "FilingController error:";

    @Autowired
    private FilingService filingService;

    @GetMapping("/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings")
    public ResponseEntity generateFiling(@PathParam("transactionId") String transactionId,
        @PathParam("companyAccountId") String accountId, HttpServletRequest request) {

        Transaction transaction =
            (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        if (transaction == null) {
            logRequestError(request, "no transaction in request session");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CompanyAccount companyAccount =
            (CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        if (companyAccount == null) {
            logRequestError(request, "no company account in request session");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Filing filing = filingService.generateAccountFiling(transaction, companyAccount);
        if (filing != null) {
            return new ResponseEntity<>(Arrays.asList(filing), HttpStatus.OK);
        }

        logRequestError(request, "Failed to generate filing");
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void logRequestError(HttpServletRequest request, String errorMessage) {

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap.put("message", FILING_CONTROLLER_ERROR + errorMessage);
        LOGGER.errorRequest(request, null, debugMap);
    }
}