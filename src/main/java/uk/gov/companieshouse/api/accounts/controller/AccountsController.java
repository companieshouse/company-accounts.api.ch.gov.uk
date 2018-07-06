package uk.gov.companieshouse.api.accounts.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;
import uk.gov.companieshouse.api.accounts.service.AccountsService;

@RestController
public class AccountsController {

    @Autowired
    private AccountsService accountsService;

    @PostMapping(value = "/transactions/{transactionId}/accounts",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createAccount(@Valid @RequestBody Accounts accounts) {
        return accountsService.createAccount(accounts);
    }
}
