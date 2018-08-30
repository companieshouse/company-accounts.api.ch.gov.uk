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
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.filing.Filing;
import uk.gov.companieshouse.api.accounts.service.FilingService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
public class FilingController {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private FilingService filingService;

    @GetMapping("/private/transactions/{transactionId}/company-accounts/{companyAccountId}/filings")
    public ResponseEntity generateFiling(@PathParam("transactionId") String transactionId,
        @PathParam("companyAccountId") String accountId, HttpServletRequest request) {

        Filing filing = filingService.generateAccountFiling(transactionId, accountId);
        if (filing != null) {
            return new ResponseEntity<>(Arrays.asList(filing), HttpStatus.OK);
        }

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("request_method", request.getMethod());
        debugMap.put("message", "FilingController error: Failed to generate filing");
        LOGGER.errorRequest(request, null, debugMap);

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}