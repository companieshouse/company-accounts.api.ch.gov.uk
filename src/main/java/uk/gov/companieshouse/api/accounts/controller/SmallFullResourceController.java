package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public class SmallFullResourceController<T extends RestObject> {

    private ResourceService<T> resourceService;

    private SmallFullLinkType smallFullLinkType;

    private ErrorMapper errorMapper;

    private ApiResponseMapper apiResponseMapper;

    public SmallFullResourceController(ResourceService<T> resourceService,
                                       SmallFullLinkType smallFullLinkType,
                                       ErrorMapper errorMapper,
                                       ApiResponseMapper apiResponseMapper) {

        this.resourceService = resourceService;
        this.smallFullLinkType = smallFullLinkType;
        this.errorMapper = errorMapper;
        this.apiResponseMapper = apiResponseMapper;

    }

    private SmallFullResourceController() {}

    public ResponseEntity create(T data,
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
            ResponseObject<T> response =
                    resourceService.create(data, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create resource: " + smallFullLinkType.getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<T> response = resourceService.find(companyAccountId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve resource: " + smallFullLinkType.getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    public ResponseEntity update(T data,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());
        if (smallFull.getLinks().get(smallFullLinkType.getLink()) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<T> response =
                    resourceService.update(data, transaction, companyAccountId, request);

            return apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update resource: " + smallFullLinkType.getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<T> response = resourceService.delete(companyAccountsId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(),
                    response.getErrors());
        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountsId, transaction,
                    "Failed to delete resource: " + smallFullLinkType.getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
