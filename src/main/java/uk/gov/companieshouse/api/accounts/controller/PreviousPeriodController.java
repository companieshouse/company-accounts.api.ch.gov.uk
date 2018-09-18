package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.service.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/previous-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreviousPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private PreviousPeriodService previousPeriodService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody PreviousPeriod previousPeriod,
        HttpServletRequest request) {

        Transaction transaction = (Transaction) request
            .getAttribute(AttributeName.TRANSACTION.getValue());

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        String companyAccountId = companyAccountEntity.getId();
        String requestId = request.getHeader("X-Request-Id");

        ResponseEntity responseEntity;
        try {
            ResponseObject<PreviousPeriod> responseObject = previousPeriodService.create(previousPeriod, transaction, companyAccountId, requestId);
            responseEntity = apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getValidationErrorData());
        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }
}
