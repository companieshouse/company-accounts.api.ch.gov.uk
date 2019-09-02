package uk.gov.companieshouse.api.accounts.controller;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CicApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/cic-report/cic-approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class CicApprovalController {

    @Autowired
    private CicApprovalService cicApprovalService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid CicApproval cicApproval,
        BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicApproval> responseObject = cicApprovalService
                .create(cicApproval, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                "Failed to create cic report approval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }

    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicApproval> response = cicApprovalService
                .find(companyAccountId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                "Failed to retrieve cic report approval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid CicApproval cicApproval,
        BindingResult bindingResult,
        @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        CicReport cicReport = (CicReport) request.getAttribute(AttributeName.CIC_REPORT.getValue());
        if (cicReport.getLinks().get(CicReportLinkType.APPROVAL.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicApproval> response = cicApprovalService
                .update(cicApproval, transaction, companyAccountId, request);

            return apiResponseMapper
                .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                "Failed to update cic report approval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicApproval> response = cicApprovalService
                .delete(companyAccountsId, request);

            return apiResponseMapper
                .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountsId, transaction,
                "Failed to delete cic report approval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

}
