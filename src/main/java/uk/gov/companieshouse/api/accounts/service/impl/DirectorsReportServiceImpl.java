package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;
import uk.gov.companieshouse.api.accounts.repository.DirectorsReportRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsReportTransformer;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Service
public class DirectorsReportServiceImpl implements DirectorsReportService {

    private DirectorsReportRepository directorsReportRepository;
    private DirectorsReportTransformer directorsReportTransformer;

    @Autowired
    public DirectorsReportServiceImpl(
            DirectorsReportRepository directorsReportRepository, DirectorsReportTransformer directorsReportTransformer) {
        this.directorsReportRepository = directorsReportRepository;
        this.directorsReportTransformer = directorsReportTransformer;
    }

    @Override
    public ResponseObject<DirectorsReport> create(DirectorsReport rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<DirectorsReport> update(DirectorsReport rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<DirectorsReport> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<DirectorsReport> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public void addDirector(String companyAccountsID, String directorID, String link, HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeDirector(String companyAccountsID, String directorID, HttpServletRequest request) throws DataException {

    }

    @Override
    public void addSecretary(String companyAccountsID, String directorID, String link, HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeSecretary(String companyAccountsID, String directorID, HttpServletRequest request) throws DataException {

    }
}
