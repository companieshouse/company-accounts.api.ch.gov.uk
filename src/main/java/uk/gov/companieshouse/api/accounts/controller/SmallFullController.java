package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full",
    produces = MediaType.APPLICATION_JSON_VALUE)
public class SmallFullController {

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private SmallFullTransformer smallFullTransformer;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody SmallFull smallFull,
        @PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<SmallFull> responseObject = smallFullService
                .create(smallFull, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create small full resource", ex, request);
            return apiResponseMapper.map(ex);
        }
    }

    @GetMapping
    public ResponseEntity get(HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request
            .getAttribute(AttributeName.SMALLFULL.getValue());

        return apiResponseMapper.mapGetResponse(smallFull, request);
    }
}