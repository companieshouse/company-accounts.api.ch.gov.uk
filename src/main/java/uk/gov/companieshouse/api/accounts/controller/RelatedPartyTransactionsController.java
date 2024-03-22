package uk.gov.companieshouse.api.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.service.impl.RelatedPartyTransactionsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/related-party-transactions")
public class RelatedPartyTransactionsController {

    @Autowired
    private RelatedPartyTransactionsServiceImpl relatedPartyTransactionsService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@RequestBody RelatedPartyTransactions relatedPartyTransactions,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<RelatedPartyTransactions> response = relatedPartyTransactionsService
                    .create(relatedPartyTransactions, transaction, companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch(DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create related party transactions resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId, HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<RelatedPartyTransactions> response = relatedPartyTransactionsService
                    .find(companyAccountId, request);
            return apiResponseMapper.mapGetResponse(response.getData(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve a related party transactions resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<RelatedPartyTransactions> response = relatedPartyTransactionsService
                    .delete(companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch(DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to delete related party transactions resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
