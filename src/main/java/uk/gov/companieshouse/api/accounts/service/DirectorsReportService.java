package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.DataException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface DirectorsReportService {

    void addDirector(String companyAccountsId, String directorId, String link, HttpServletRequest request)
            throws DataException;

    void removeDirector(String companyAccountsId, String directorId, HttpServletRequest request) throws DataException;
}
