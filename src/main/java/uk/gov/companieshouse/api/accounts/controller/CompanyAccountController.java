package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@RestController
public class CompanyAccountController {

    @Autowired
    private CompanyAccountService companyAccountService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts",
        consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader("X-Request-Id");
        ResponseObject result = companyAccountService.createCompanyAccount(companyAccount, transaction, requestId);
return apiResponseMapper
        .map(result.getStatus(), result.getData(), result.getErrorData());
    }

    @GetMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCompanyAccount(HttpServletRequest request) {

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request.getSession()
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        CompanyAccount companyAccount = companyAccountTransformer.transform(companyAccountEntity);
        return ResponseEntity.status(HttpStatus.OK).body(companyAccount);

    }
}
