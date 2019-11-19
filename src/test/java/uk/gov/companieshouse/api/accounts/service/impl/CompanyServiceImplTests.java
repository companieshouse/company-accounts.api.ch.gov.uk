package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.sdk.ApiClientService;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.company.account.CompanyAccountApi;
import uk.gov.companieshouse.api.model.company.account.LastAccountsApi;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyServiceImplTests {

    @Mock
    private ApiClientService mockApiClientService;

    @Mock
    private ApiClient mockApiClient;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CompanyResourceHandler mockCompanyResourceHandler;

    @Mock
    private CompanyGet mockCompanyGet;

    @Mock
    private CompanyProfileApi mockCompanyProfileApi;

    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;

    @InjectMocks
    private CompanyService companyService = new CompanyServiceImpl();

    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_URI = "/company/" + COMPANY_NUMBER;

    private static final String LIMITED_BY_GUARANTEE = "private-limited-guarant-nsc";
    private static final String LIMITED_BY_GUARANTEE_EXEMPT = "private-limited-guarant-nsc-limited-exemption";
    private static final String PLC = "plc";

    @BeforeEach
    private void init() {
        when(mockApiClientService.getApiClient()).thenReturn(mockApiClient);
        when(mockApiClient.company()).thenReturn(mockCompanyResourceHandler);
        when(mockCompanyResourceHandler.get(COMPANY_URI)).thenReturn(mockCompanyGet);
    }

    @Test
    @DisplayName("Get Company Profile - Success Path")
    void getCompanyProfileSuccess() throws Exception {

        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(new CompanyProfileApi());
        CompanyProfileApi companyProfile = companyService.getCompanyProfile(COMPANY_NUMBER);

        assertNotNull(companyProfile);
    }

    @Test
    @DisplayName("Get Company Profile - Throws ApiErrorResponseException")
    void getCompanyProfileThrowsApiErrorResponseException() throws Exception {

        when(mockCompanyGet.execute()).thenThrow(ApiErrorResponseException.class);

        assertThrows(ServiceException.class, () ->
                companyService.getCompanyProfile(COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Get Company Profile - Throws URIValidationException")
    void getCompanyProfileThrowsURIValidationException() throws ApiErrorResponseException,
            URIValidationException {

        when(mockCompanyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                companyService.getCompanyProfile(COMPANY_NUMBER));
    }

    @Test
    @DisplayName("Multiple year filer returns true")
    void multipleYearFilerReturnsTrue() throws ServiceException, ApiErrorResponseException,
            URIValidationException {

        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(generateMultipleYearFiler());
        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        assertTrue(companyService.isMultipleYearFiler(mockTransaction));
    }

    @Test
    @DisplayName("Multiple year filer returns false")
    void multipleYearFilerReturnsFalse() throws ServiceException, ApiErrorResponseException,
            URIValidationException {

        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(new CompanyProfileApi());
        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        assertFalse(companyService.isMultipleYearFiler(mockTransaction));
    }

    @Test
    @DisplayName("First year filer returns true")
    void firstYearFilerReturnsFalse() throws ServiceException, ApiErrorResponseException,
            URIValidationException {

        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(new CompanyProfileApi());
        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        assertFalse(companyService.isMultipleYearFiler(mockTransaction));
    }

    @Test
    @DisplayName("Service error thrown when method fails")
    void serviceErrorThrownWhenFails() throws ApiErrorResponseException, URIValidationException {

        when(mockCompanyGet.execute()).thenThrow(URIValidationException.class);
        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        assertThrows(ServiceException.class, () ->
                companyService.isMultipleYearFiler(mockTransaction));
    }

    @Test
    @DisplayName("isCIC returns true")
    void isCICReturnsTrue() throws ServiceException, ApiErrorResponseException,
            URIValidationException {

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(mockCompanyProfileApi);
        when(mockCompanyProfileApi.isCommunityInterestCompany()).thenReturn(true);

        assertTrue(companyService.isCIC(mockTransaction));
    }

    @Test
    @DisplayName("isCIC returns false")
    void isCICReturnsFalse() throws ServiceException, ApiErrorResponseException,
            URIValidationException {

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(mockCompanyProfileApi);
        when(mockCompanyProfileApi.isCommunityInterestCompany()).thenReturn(false);

        assertFalse(companyService.isCIC(mockTransaction));
    }

    @Test
    @DisplayName("isLBG returns true for private limited by guarantee company")
    void isLBGForPrivateLimitedByGuaranteeCompany()
            throws ApiErrorResponseException, URIValidationException, ServiceException {

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(mockCompanyProfileApi);
        when(mockCompanyProfileApi.getType()).thenReturn(LIMITED_BY_GUARANTEE);

        assertTrue(companyService.isLBG(mockTransaction));
    }

    @Test
    @DisplayName("isLBG returns true for private limited by guarantee exempt company")
    void isLBGForPrivateLimitedByGuaranteeExemptCompany()
            throws ApiErrorResponseException, URIValidationException, ServiceException {

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(mockCompanyProfileApi);
        when(mockCompanyProfileApi.getType()).thenReturn(LIMITED_BY_GUARANTEE_EXEMPT);

        assertTrue(companyService.isLBG(mockTransaction));
    }

    @Test
    @DisplayName("isLBG returns false for plc company")
    void isLBGForPLCCompany()
            throws ApiErrorResponseException, URIValidationException, ServiceException {

        when(mockTransaction.getCompanyNumber()).thenReturn(COMPANY_NUMBER);
        when(mockCompanyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(mockCompanyProfileApi);
        when(mockCompanyProfileApi.getType()).thenReturn(PLC);

        assertFalse(companyService.isLBG(mockTransaction));
    }

    private CompanyProfileApi generateMultipleYearFiler() {

        CompanyProfileApi companyProfileApi = new CompanyProfileApi();
        CompanyAccountApi companyAccountApi = new CompanyAccountApi();

        LastAccountsApi lastAccountsApi = new LastAccountsApi();
        lastAccountsApi.setType("lastaccounts");
        lastAccountsApi.setPeriodStartOn(LocalDate.now());

        companyAccountApi.setLastAccounts(lastAccountsApi);
        companyProfileApi.setAccounts(companyAccountApi);

        return companyProfileApi;
    }

}