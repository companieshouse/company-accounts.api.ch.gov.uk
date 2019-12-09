package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/directors-report/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class DirectorsApprovalController {

    @Autowired
    private DirectorsApprovalService directorsApprovalService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid DirectorsApproval directorsApproval,
                                 @PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<DirectorsApproval> responseObject = directorsApprovalService.create(directorsApproval, transaction, companyAccountId, request);
            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());

        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to create directorsApproval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody DirectorsApproval directorsApproval,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        DirectorsReport directorsReport = (DirectorsReport) request.getAttribute(AttributeName.DIRECTORS_REPORT.getValue());
        if(directorsReport.getLinks().get(DirectorsReportLinkType.APPROVAL.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Transaction transaction =
                (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<DirectorsApproval> responseObject =
                    directorsApprovalService.update(directorsApproval, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update directorsApproval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<DirectorsApproval> responseObject = directorsApprovalService.find(companyAccountId, request);
            return apiResponseMapper.mapGetResponse(responseObject.getData(), request);
        } catch(DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction, "Failed to get directorsApproval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<DirectorsApproval> response = directorsApprovalService.delete(companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch(DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to delete directorsApproval resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
