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
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DebtorsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/debtors", produces = MediaType.APPLICATION_JSON_VALUE)
public class DebtorsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);
    private static final String TRANSACTION_ID = "transaction_id";
    private static final String COMPANY_ACCOUNT_ID = "company_account_id";
    private static final String MESSAGE = "message";

    @Autowired
    private DebtorsService debtorsService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Debtors debtors,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction =
            (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity;

        try {
            ResponseObject<Debtors> response = debtorsService
                .create(debtors, transaction, companyAccountId, request);

            responseEntity = apiResponseMapper
                .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            final Map<String, Object> debugMap = createDebugMap(companyAccountId, transaction, "Failed to create debtors resource");
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }
        return responseEntity;
    }


    private Map<String, Object> createDebugMap(@PathVariable("companyAccountId") String companyAccountId, Transaction transaction, String s) {
        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(TRANSACTION_ID, transaction.getId());
        debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
        debugMap.put(MESSAGE, s);
        return debugMap;
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        String accountingPoliciesId = debtorsService.generateID(companyAccountId);

        ResponseEntity responseEntity;

        try {
            ResponseObject<Debtors> response = debtorsService
                .findById(accountingPoliciesId, request);

            responseEntity = apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException de) {

            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put(TRANSACTION_ID, transaction.getId());
            debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
            debugMap.put(MESSAGE, "Failed to retrieve debtors resource");
            LOGGER.errorRequest(request, de, debugMap);
            responseEntity = apiResponseMapper.map(de);
        }

        return responseEntity;
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Debtors debtors,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFull.getLinks().get(SmallFullLinkType.DEBTORS_NOTE.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Debtors> response = debtorsService
                .update(debtors, transaction, companyAccountId, request);

            return apiResponseMapper
                .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put(TRANSACTION_ID, transaction.getId());
            debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
            debugMap.put(MESSAGE, "Failed to update debtors resource");
            LOGGER.errorRequest(request, ex, debugMap);
            return apiResponseMapper.map(ex);
        }
    }
}
