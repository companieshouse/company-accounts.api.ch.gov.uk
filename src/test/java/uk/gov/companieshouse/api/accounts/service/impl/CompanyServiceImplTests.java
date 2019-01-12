package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.accounts.api.ApiClientService;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompanyServiceImplTests {

    @Mock
    private ApiClientService mockApiClientService;

    @Mock
    private ApiClient mockApiClient;

    @Mock
    private CompanyResourceHandler mockCompanyResourceHandler;

    @Mock
    private CompanyGet mockCompanyGet;

    @Mock
    private Transaction mockTransaction;

    @InjectMocks
    private CompanyService companyService = new CompanyServiceImpl();

    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_URI = "/company/" + COMPANY_NUMBER;

    @BeforeEach
    private void init() {
        when(mockApiClientService.getApiClient()).thenReturn(mockApiClient);
        when(mockApiClient.company()).thenReturn(mockCompanyResourceHandler);
        when(mockCompanyResourceHandler.get(COMPANY_URI)).thenReturn(mockCompanyGet);
    }

    @Test
    @DisplayName("Get Company Profile - Success Path")
    void getCompanyProfileSuccess() throws Exception {

        when(mockCompanyGet.execute()).thenReturn(new CompanyProfileApi());
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
    void getBalanceSheetThrowsURIValidationException() throws ApiErrorResponseException,
            URIValidationException {

        when(mockCompanyGet.execute()).thenThrow(URIValidationException.class);

        assertThrows(ServiceException.class, () ->
                companyService.getCompanyProfile(COMPANY_NUMBER));
    }
}