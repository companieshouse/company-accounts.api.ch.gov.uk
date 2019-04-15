package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

public interface CompanyService {

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;

    /**
     * Return whether company profile has a last accounts and is therefore a multiple year filer.
     *
     * @param transaction Transaction information
     */
    boolean isMultipleYearFiler(Transaction transaction) throws ServiceException;

    /**
     * Return whether company profile is a community interest company.
     *
     * @param transaction Transaction for which to look up company details
     * @return whether the company associated with the transaction is a CIC
     * @throws ServiceException if there's an error when fetching the company profile
     */
    boolean isCIC(Transaction transaction) throws ServiceException;
}
