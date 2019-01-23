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
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogHelper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/current-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrentPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CurrentPeriod currentPeriod,
        BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            if (errors.hasErrors()) {
                LOGGER.error("Current period validation failure");
                logValidationFailureError(request, errors);
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity;

        try {
            ResponseObject<CurrentPeriod> responseObject = currentPeriodService
                .create(currentPeriod, transaction, companyAccountId, request);
            responseEntity = apiResponseMapper
                .map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid CurrentPeriod currentPeriod,
        BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFull.getLinks().get(SmallFullLinkType.CURRENT_PERIOD.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            if (errors.hasErrors()) {
                LOGGER.error("Current period validation failure");
                logValidationFailureError(request, errors);
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity;

        try {
            ResponseObject<CurrentPeriod> responseObject = currentPeriodService
                .update(currentPeriod, transaction, companyAccountId, request);
            responseEntity = apiResponseMapper
                .map(responseObject.getStatus(), null, responseObject.getErrors());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        LogHelper.createNewLogContext(request);

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String currentPeriodId = currentPeriodService.generateID(companyAccountId);
        ResponseObject<CurrentPeriod> responseObject;

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            responseObject = currentPeriodService.findById(currentPeriodId, request);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }

        return apiResponseMapper.mapGetResponse(responseObject.getData(), request);

    }

    private void logValidationFailureError(HttpServletRequest request, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceRequest(request, "", logMap);
    }
}
