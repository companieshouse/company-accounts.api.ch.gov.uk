package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/current-period", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrentPeriodController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CurrentPeriod currentPeriod,
            HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        String companyAccountId = companyAccountEntity.getId();
        String requestId = request.getHeader("X-Request-Id");

        ResponseEntity responseEntity;
        try {
            ResponseObject<CurrentPeriod> responseObject = currentPeriodService
                    .create(currentPeriod, transaction, companyAccountId, requestId);
            responseEntity = apiResponseMapper
                    .map(responseObject.getStatus(), responseObject.getData(),
                            responseObject.getValidationErrorData());
        } catch (DataException ex) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("transaction_id", transaction.getId());
            LOGGER.errorRequest(request, ex, debugMap);
            responseEntity = apiResponseMapper.map(ex);
        }

        return responseEntity;
    }

    @GetMapping
    public ResponseEntity get(HttpServletRequest request) throws NoSuchAlgorithmException {
        LogContext logContext = LogHelper.createNewLogContext(request);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());
        CompanyAccountEntity companyAccountEntity = (CompanyAccountEntity) request
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        if (companyAccountEntity == null) {
            LOGGER.error("Current Period error: No company account in request session",
                    logContext);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String companyAccountId = companyAccountEntity.getId();
        String currentPeriodId = currentPeriodService.generateID(companyAccountId);
        ResponseObject<CurrentPeriod> responseObject;

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put("transaction_id", transaction.getId());

        try {
            responseObject = currentPeriodService.findById(currentPeriodId);
        } catch (DataAccessException dae) {
            LOGGER.errorRequest(request, dae, debugMap);
            return apiResponseMapper.map(dae);
        }

        CurrentPeriod currentPeriod = responseObject.getData();
        return apiResponseMapper.mapGetResponse(currentPeriod, request);

    }
}
