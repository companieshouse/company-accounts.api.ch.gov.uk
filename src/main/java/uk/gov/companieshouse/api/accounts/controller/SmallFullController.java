package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@RestController
public class SmallFullController {

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@Valid @RequestBody SmallFull smallFull,
            HttpServletRequest request) {
        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseObject<SmallFull> result = smallFullService
                .save(smallFull, transaction.getCompanyNumber());
        return apiResponseMapper.map(result.getStatus(), result.getData(), result.getErrorData());
    }
}