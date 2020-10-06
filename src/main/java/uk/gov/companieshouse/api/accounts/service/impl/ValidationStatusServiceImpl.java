package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.ValidationStatusService;
import uk.gov.companieshouse.api.accounts.validation.AccountsValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Service
public class ValidationStatusServiceImpl implements ValidationStatusService {

    @Autowired
    private AccountsValidator accountsValidator;

    @Override
    public Errors getValidationErrors(String companyAccountsId, HttpServletRequest request) throws DataException {

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        return accountsValidator.validate(transaction, companyAccountsId, request);
    }
}
