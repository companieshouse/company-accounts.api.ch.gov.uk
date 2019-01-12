package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

public interface CompanyService {

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;

    /**
     * Return whether company profile has a last accounts and is therefore a multiple year filer.
     *
     * @param transaction - Transaction information
     */
    boolean isMultipleYearFiler(Transaction transaction) throws ServiceException;
}
