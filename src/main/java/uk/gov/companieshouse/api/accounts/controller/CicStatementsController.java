package uk.gov.companieshouse.api.accounts.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CicStatementsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/cic-report/cic-statements", produces = MediaType.APPLICATION_JSON_VALUE)
public class CicStatementsController {

    @Autowired
    private CicStatementsService cicStatementsService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CicStatements cicStatements,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicStatements> response = cicStatementsService
                    .create(cicStatements, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create CIC statements resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicStatements> response = cicStatementsService.find(companyAccountId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve CIC statements resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid CicStatements cicStatements,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {
        CicReport cicReport = (CicReport) request.getAttribute(AttributeName.CIC_REPORT.getValue());

        if (cicReport.getLinks().get(CicReportLinkType.STATEMENTS.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicStatements> response = cicStatementsService
                    .update(cicStatements, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update CIC statements resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicStatements> response = cicStatementsService.delete(companyAccountsId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountsId, transaction,
                    "Failed to delete CIC statements resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
