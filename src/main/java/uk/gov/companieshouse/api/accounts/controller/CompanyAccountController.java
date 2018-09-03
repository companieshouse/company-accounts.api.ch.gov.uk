package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
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

    @PostMapping
    public ResponseEntity createCompanyAccount(@Valid @RequestBody CompanyAccount companyAccount,
            HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader("X-Request-Id");

        ResponseEntity responseEntity;
        try {
            ResponseObject<CompanyAccount> responseObject = companyAccountService
                    .createCompanyAccount(companyAccount, transaction, requestId);
            responseEntity = apiResponseMapper
                    .map(responseObject.getStatus(), responseObject.getData(),
                            responseObject.getValidationErrorData());
        } catch (PatchException | DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    @GetMapping("/{companyAccountId}")
    public ResponseEntity getCompanyAccount(HttpServletRequest request) {
        LogContext logContext = LogHelper.createNewLogContext(request);

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        if (companyAccountEntity == null) {

            LOGGER.error("CompanyAccountController error: No company account in request",
                    logContext);
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(companyAccountTransformer.transform(companyAccountEntity));

    }
}
