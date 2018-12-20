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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;
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

    @Autowired
    private PreviousPeriodValidator previousPeriodValidator;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody PreviousPeriod previousPeriod,
        BindingResult bindingResult,
        @PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Errors errors = previousPeriodValidator.validatePreviousPeriod(previousPeriod);
        if (errors.hasErrors()) {

            LOGGER.error(
                "Previous period validation failure");
            logValidationFailureError(request, errors);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

        }

        Transaction transaction = getTransactionFromRequest(request);
        ResponseEntity responseEntity;

        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService
                .create(previousPeriod, transaction, companyAccountId, request);
            responseEntity = apiResponseMapper
                .map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

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

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {
        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String previousPeriodId = previousPeriodService.generateID(companyAccountId);
        ResponseObject<PreviousPeriod> responseObject;

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            responseObject = previousPeriodService.findById(previousPeriodId, request);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }

        return apiResponseMapper.mapGetResponse(responseObject.getData(), request);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid PreviousPeriod previousPeriod,
            BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
            HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFull.getLinks().get(SmallFullLinkType.PREVIOUS_PERIOD.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Errors errors = previousPeriodValidator.validatePreviousPeriod(previousPeriod);
        if (errors.hasErrors()) {
            LOGGER.info( "Previous period validation failure");
            logValidationFailureError(request, errors);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity;

        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService.update(previousPeriod, transaction, companyAccountId, request);
            responseEntity = apiResponseMapper.map(responseObject.getStatus(), null, responseObject.getErrors());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    private Transaction getTransactionFromRequest(HttpServletRequest request) {
        return (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
    }

    void logValidationFailureError(HttpServletRequest request, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceRequest(request, "", logMap);
    }
}
