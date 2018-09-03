package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogHelper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class SmallFullController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private SmallFullTransformer smallFullTransformer;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody SmallFull smallFull,
            HttpServletRequest request) {
        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseObject<SmallFull> result = smallFullService
                .save(smallFull, transaction.getCompanyNumber());
        return apiResponseMapper
                .map(result.getStatus(), result.getData(), result.getValidationErrorData());
    }

    @GetMapping
    public ResponseEntity get(HttpServletRequest request) {

        LogContext logContext = LogHelper.createNewLogContext(request);

        SmallFullEntity smallFullEntity = (SmallFullEntity) request
                .getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFullEntity == null) {

            LOGGER.error("SmallFullTransformer error: No small-full account in request",
                    logContext);
            return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(null);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(smallFullTransformer.transform(smallFullEntity));
    }
}