package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/directors-report", produces = MediaType.APPLICATION_JSON_VALUE)
public class DirectorsReportController {

    @Autowired
    private DirectorsReportServiceImpl directorsReportService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody DirectorsReport directorsReport,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<DirectorsReport> response = directorsReportService.create(
                    directorsReport, transaction, companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch(DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to create directorsReport resource",
                    ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<DirectorsReport> response = directorsReportService.find(companyAccountId, request);
            return apiResponseMapper.mapGetResponse(response.getData(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve a directorsReport resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());
        try {
            ResponseObject<DirectorsReport> response = directorsReportService.delete(companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch(DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to delete directorsReport resource",
                    ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
