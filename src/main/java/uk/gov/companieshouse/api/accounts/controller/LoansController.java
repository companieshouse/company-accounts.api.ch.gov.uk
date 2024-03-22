package uk.gov.companieshouse.api.accounts.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.LoanServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@RestController
@RequestMapping("/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/loans-to-directors/loans")
public class LoansController {

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ErrorMapper errorMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Loan loan, BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Loan> response = loanService.create(loan, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to create a Loan resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping("/{loanId}")
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Loan> response = loanService.find(companyAccountId, request);

            return apiResponseMapper.mapGetResponse(response.getData(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve a Loan resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity getAll(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Loan> response = loanService.findAll(transaction, companyAccountId, request);

            return apiResponseMapper.mapGetResponseForMultipleResources(response.getDataForMultipleResources(), request);
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve Loans", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping("/{loanId}")
    public ResponseEntity update(@Valid @RequestBody Loan loan, BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("loanId") String loanId,
                                 HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        LoansToDirectors loansToDirectors = (LoansToDirectors)
                request.getAttribute(AttributeName.LOANS_TO_DIRECTORS.getValue());
        if (loansToDirectors.getLoans().get(loanId) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Loan> response = loanService.update(loan, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to update Loan resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping("/{loanId}")
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Loan> response = loanService.delete(companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {
            LoggingHelper.logException(companyAccountId, transaction, "Failed to delete Loan resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
