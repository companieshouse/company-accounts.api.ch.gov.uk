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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.TangibleAssetsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/tangible-assets", produces = MediaType.APPLICATION_JSON_VALUE)
public class TangibleAssetsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private TangibleAssetsService tangibleAssetsService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    private static final String TRANSACTION_ID = "transaction_id";

    private static final String COMPANY_ACCOUNT_ID = "company_account_id";

    private static final String MESSAGE = "message";

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody TangibleAssets tangibleAssets,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction =
                (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<TangibleAssets> response =
                    tangibleAssetsService
                            .create(tangibleAssets, transaction, companyAccountId, request);

            return apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            final Map<String, Object> debugMap =
                    createDebugMap(companyAccountId, transaction, "Failed to create tangible assets resource");
            LOGGER.errorRequest(request, ex, debugMap);
            return apiResponseMapper.map(ex);
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        String tangibleAssetsId = tangibleAssetsService.generateID(companyAccountId);

        try {
            ResponseObject<TangibleAssets> response =
                    tangibleAssetsService
                        .findById(tangibleAssetsId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException de) {

            final Map<String, Object> debugMap =
                    createDebugMap(companyAccountId, transaction, "Failed to retrieve tangible assets resource");
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid TangibleAssets tangibleAssets,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFull.getLinks().get(SmallFullLinkType.TANGIBLE_ASSETS_NOTE.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<TangibleAssets> response =
                    tangibleAssetsService
                            .update(tangibleAssets, transaction, companyAccountId, request);

            return apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            final Map<String, Object> debugMap =
                    createDebugMap(companyAccountId, transaction, "Failed to update tangible assets resource");
            LOGGER.errorRequest(request, ex, debugMap);
            return apiResponseMapper.map(ex);
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<TangibleAssets> response =
                    tangibleAssetsService.delete(companyAccountsId, request);

            return apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException de) {

            final Map<String, Object> debugMap = createDebugMap(companyAccountsId, transaction,
                    "Failed to delete tangible assets resource");
            LOGGER.errorRequest(request, de, debugMap);
            return apiResponseMapper.map(de);
        }
    }

    private Map<String, Object> createDebugMap(String companyAccountId, Transaction transaction, String message) {

        final Map<String, Object> debugMap = new HashMap<>();
        debugMap.put(TRANSACTION_ID, transaction.getId());
        debugMap.put(COMPANY_ACCOUNT_ID, companyAccountId);
        debugMap.put(MESSAGE, message);
        return debugMap;
    }
}