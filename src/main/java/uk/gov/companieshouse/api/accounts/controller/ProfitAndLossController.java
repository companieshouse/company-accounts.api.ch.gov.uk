package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ProfitAndLossService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/{accountingPeriod:current-period|previous-period}/profit-and-loss", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfitAndLossController {

    @Autowired
    private ProfitAndLossService profitAndLossService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private PeriodConverter periodConverter;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody ProfitAndLoss profitAndLoss,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request, AccountingPeriod period) {

        if(bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return  new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<ProfitAndLoss> response = profitAndLossService.create(profitAndLoss, transaction,
                    companyAccountId, request, period);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to create profit and loss resource",
                    ex, request);
            return apiResponseMapper.getErrorResponse();

        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request, AccountingPeriod period) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<ProfitAndLoss> response = profitAndLossService.find(companyAccountId, period);
            return apiResponseMapper.mapGetResponse(response.getData(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to retrieve profit and loss resource",
                    ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid ProfitAndLoss profitAndLoss, BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId, @PathVariable("accountingPeriod") AccountingPeriod accountingPeriod, HttpServletRequest request) {

        if (accountingPeriod.equals(AccountingPeriod.CURRENT_PERIOD)) {
            CurrentPeriod currentPeriod = (CurrentPeriod)
                    request.getAttribute(AttributeName.CURRENT_PERIOD.getValue());
            if(currentPeriod.getLinks().get(CurrentPeriodLinkType.PROFIT_AND_LOSS.getLink()) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            PreviousPeriod previousPeriod = (PreviousPeriod)
                    request.getAttribute(AttributeName.PREVIOUS_PERIOD.getValue());
            if(previousPeriod.getLinks().get(PreviousPeriodLinkType.PROFIT_AND_LOSS.getLink()) == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        if(bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return  new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<ProfitAndLoss> response =
                    profitAndLossService.update(profitAndLoss, transaction, companyAccountId, request, accountingPeriod);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update profit and loss resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request, AccountingPeriod period) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<ProfitAndLoss> response = profitAndLossService.delete(companyAccountId, request, period);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to delete profit and loss resource",
                    ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @InitBinder
    protected void initBinder(final WebDataBinder webdataBinder) {

        webdataBinder.registerCustomEditor(AccountingPeriod.class, periodConverter);
    }

}