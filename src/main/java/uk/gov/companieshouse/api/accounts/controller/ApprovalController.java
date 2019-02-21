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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody @Valid Approval approval,
        BindingResult bindingResult, @PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Approval> responseObject = approvalService
                .create(approval, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create approval resource", ex, request);
            return apiResponseMapper.map(ex);
        }

    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String approvalId = approvalService.generateID(companyAccountId);

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            ResponseObject<Approval> response = approvalService.findById(approvalId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve approval resource", ex, request);
            return apiResponseMapper.map(ex);
        }
    }

}
