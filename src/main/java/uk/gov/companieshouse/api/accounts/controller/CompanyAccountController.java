package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@RestController
public class CompanyAccountController {

    @Autowired
    private CompanyAccountService companyAccountService;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
            HttpServletRequest request)
            throws NoSuchAlgorithmException {
        Transaction transaction = (Transaction) request.getAttribute("transaction");
        CompanyAccount result = companyAccountService
                .save(companyAccount, transaction.getCompanyNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
