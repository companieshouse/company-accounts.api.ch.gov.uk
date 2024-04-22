package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.CompanyAccountValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;


@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class CompanyAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private CompanyAccountService companyAccountService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private CompanyAccountTransformer companyAccountTransformer;

    @Autowired
    private CompanyAccountValidator validator;

    @PostMapping
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
                                               HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            Errors errors = validator.validateCompanyAccount(transaction);
            if (errors.hasErrors()) {
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            ResponseObject<CompanyAccount> responseObject = companyAccountService
                .create(companyAccount, transaction, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());
        } catch (PatchException | DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping("/{companyAccountId}")
    public ResponseEntity getCompanyAccount(HttpServletRequest request) {
        LogContext logContext = LogHelper.createNewLogContext(request);

        CompanyAccount companyAccount = (CompanyAccount) request
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        if (companyAccount == null) {
            LOGGER.error("CompanyAccountController error: No company account in request",
                logContext);
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK).body(companyAccount);

    }
}
