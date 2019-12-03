package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

public interface DirectorsReportService {

    ResponseObject<DirectorsReport> create(DirectorsReport rest, Transaction transaction, String companyAccountsId,
                             HttpServletRequest request)
            throws DataException;

    ResponseObject<DirectorsReport> update(DirectorsReport rest, Transaction transaction, String companyAccountsId,
                             HttpServletRequest request)
            throws DataException;

    ResponseObject<DirectorsReport> find(String companyAccountsId, HttpServletRequest request)
            throws DataException;

    ResponseObject<DirectorsReport> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException;

    void addDirector(String companyAccountsID, String directorID, String link, HttpServletRequest request)
            throws DataException;

    void removeDirector(String companyAccountsID, String directorID, HttpServletRequest request) throws DataException;

    void addSecretary(String companyAccountsID, String secretaryId, String link, HttpServletRequest request)
            throws DataException;

    void removeSecretary(String companyAccountsID, String secretaryId, HttpServletRequest request) throws DataException;
}
