package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.api.LogContext;
import uk.gov.companieshouse.logging.api.LogHelper;
import uk.gov.companieshouse.logging.api.LogType;
import uk.gov.companieshouse.logging.api.LoggerApi;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyAccountController {

    @Autowired
    private LoggerApi accountsLogger;

    @Autowired
    private CompanyAccountService companyAccountService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    @PostMapping
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
            HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader("X-Request-Id");
        ResponseObject result = companyAccountService
                .createCompanyAccount(companyAccount, transaction, requestId);
        return apiResponseMapper
                .map(result.getStatus(), result.getData(), result.getErrorData());
    }

    @GetMapping
    public ResponseEntity getCompanyAccount(HttpServletRequest request) {
        LogContext logContext = LogHelper.createNewLogContext(request, LogType.ERROR);

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        if (companyAccountEntity == null) {

            accountsLogger.logError("CompanyAccountController error: No company account in request",
                    logContext);
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(companyAccountTransformer.transform(companyAccountEntity));

    }
}
