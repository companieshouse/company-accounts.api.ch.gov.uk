package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreviousPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private PreviousPeriodService previousPeriodService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;
    @Autowired
    private ErrorMapper errorMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid PreviousPeriod previousPeriod,
            @PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request, BindingResult bindingResult) {
        String requestId = getRequestId(request);
        Errors errors = new Errors();

        if (bindingResult.hasErrors()) {
            errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult, errors);
        }

        if (errors.hasErrors()) {
            LOGGER.error("Previous period validation failure");
            logValidationFailureError(requestId, errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = getTransactionFromRequest(request);
        ResponseEntity responseEntity;

        System.out.println("its here");

        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService.create(previousPeriod, transaction, companyAccountId, requestId);
            responseEntity = apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getValidationErrorData());
        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            debugMap.put("company_account_id", companyAccountId);
            debugMap.put("message", "Failed to create previous period resource");
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    private void logValidationFailureError(String requestId, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceContext(requestId, "", logMap);
    }

    private Transaction getTransactionFromRequest(HttpServletRequest request) {
        return (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
    }

    private String getRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-Id");
    }
}
