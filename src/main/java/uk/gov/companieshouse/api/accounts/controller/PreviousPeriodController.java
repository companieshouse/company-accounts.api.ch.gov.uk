package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreviousPeriodController {

    @Autowired
    private PreviousPeriodService previousPeriodService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid PreviousPeriod previousPeriod,
        BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService
                .create(previousPeriod, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create previous period resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String previousPeriodId = previousPeriodService.generateID(companyAccountId);

        try {
            ResponseObject<PreviousPeriod> response =
                    previousPeriodService.findById(previousPeriodId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve previous period resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
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

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService
                .update(previousPeriod, transaction, companyAccountId, request);

            return apiResponseMapper
                .map(responseObject.getStatus(), null, responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update previous period resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
