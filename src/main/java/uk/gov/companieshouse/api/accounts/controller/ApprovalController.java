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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/approval", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    private static final String REQUEST_ID = "X-Request-Id";

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

        LogContext logContext = LogHelper.createNewLogContext(request);
        String requestId = request.getHeader(REQUEST_ID);

        if (bindingResult.hasErrors()) {

            Errors errors = errorMapper
                .mapBindingResultErrorsToErrorModel(bindingResult, new Errors());

            if (errors.hasErrors()) {
                LOGGER.error("Approval validation failure", logContext);
                logValidationFailureError(requestId, errors);
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }
        }

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Approval> responseObject = approvalService
                .create(approval, transaction, companyAccountId, requestId);
            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getValidationErrorData());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            return apiResponseMapper.map(ex);
        }

    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String requestId = request.getHeader(REQUEST_ID);
        String approvalId = approvalService.generateID(companyAccountId);
        ResponseObject<Approval> responseObject;

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            responseObject = approvalService.findById(approvalId, requestId);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }

        return apiResponseMapper.mapGetResponse(responseObject.getData(), request);

    }

    private void logValidationFailureError(String requestId, Errors errors) {
        HashMap<String, Object> logMap = new HashMap<>();
        logMap.put("message", "Validation failure");
        logMap.put("Errors: ", errors);
        LOGGER.traceContext(requestId, "", logMap);
    }

}
