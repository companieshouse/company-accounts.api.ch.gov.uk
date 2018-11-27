package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.AccountingPoliciesService;
import uk.gov.companieshouse.api.accounts.service.impl.DebtorsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class DebtorsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private DebtorsService debtorsService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    private static final String TRANSACTION_ID = "transaction_id";

    private static final String COMPANY_ACCOUNT_ID = "company_account_id";

    private static final String MESSAGE = "message";

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Debtors debtors,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction =
                (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity;

        try {
            ResponseObject<Debtors> response = debtorsService
                    .create(debtors, transaction, companyAccountId, request);

            responseEntity = apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put(TRANSACTION_ID, transaction.getId());
            debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
            debugMap.put(MESSAGE, "Failed to create debtors resource");
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

}
