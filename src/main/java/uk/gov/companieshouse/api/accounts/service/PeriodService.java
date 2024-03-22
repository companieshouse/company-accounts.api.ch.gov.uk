package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.exception.DataException;

import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;

public interface PeriodService<T extends RestObject> {

    ResponseObject<T> create(T rest, Transaction transaction, String companyAccountId,
                             HttpServletRequest request, AccountingPeriod period)
            throws DataException;

    ResponseObject<T> update(T rest, Transaction transaction, String companyAccountId,
                             HttpServletRequest request, AccountingPeriod period)
            throws DataException;

    ResponseObject<T> find(String companyAccountsId, AccountingPeriod period)
            throws DataException;

    ResponseObject<T> delete(String companyAccountsId, HttpServletRequest request, AccountingPeriod period)
            throws DataException;
}
