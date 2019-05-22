package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.Arrays;
import java.util.List;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private ApiClientService apiClientService;

    private static final List<String> LBG_COMPANY_TYPES =
            Arrays.asList("private-limited-guarant-nsc", "private-limited-guarant-nsc-limited-exemption");

    private static final UriTemplate GET_COMPANY_URI =
            new UriTemplate("/company/{companyNumber}");

    @Override
    public CompanyProfileApi getCompanyProfile(String companyNumber) throws ServiceException {

        ApiClient apiClient = apiClientService.getApiClient();

        CompanyProfileApi companyProfileApi;

        String uri = GET_COMPANY_URI.expand(companyNumber).toString();

        try {
            companyProfileApi = apiClient.company().get(uri).execute();
        } catch (ApiErrorResponseException e) {

            throw new ServiceException("Error retrieving company profile", e);
        } catch (URIValidationException e) {

            throw new ServiceException("Invalid URI for company resource", e);
        }

        return companyProfileApi;
    }

    @Override
    public boolean isMultipleYearFiler(Transaction transaction) throws ServiceException {

        CompanyProfileApi companyProfile = getCompanyProfile(transaction.getCompanyNumber());
        return (companyProfile != null && companyProfile.getAccounts() != null &&
                companyProfile.getAccounts().getLastAccounts() != null &&
                companyProfile.getAccounts().getLastAccounts().getPeriodStartOn() != null);
    }

    @Override
    public boolean isCIC(Transaction transaction) throws ServiceException {

        CompanyProfileApi companyProfile = getCompanyProfile(transaction.getCompanyNumber());
        return companyProfile.isCommunityInterestCompany();
    }

    @Override
    public boolean isLBG(Transaction transaction) throws ServiceException {

        CompanyProfileApi companyProfile = getCompanyProfile(transaction.getCompanyNumber());
        return LBG_COMPANY_TYPES.contains(companyProfile.getType());
    }
}

