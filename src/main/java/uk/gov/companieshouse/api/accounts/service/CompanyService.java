package uk.gov.companieshouse.api.accounts.service;

import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

public interface CompanyService {

    CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException;
}
