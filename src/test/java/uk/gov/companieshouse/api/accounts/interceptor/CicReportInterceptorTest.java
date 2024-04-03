package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.impl.CicReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CicReportInterceptorTest {
    @Mock
    private CicReportService cicReportService;

    @InjectMocks
    private CicReportInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Transaction transaction;

    @Mock
    private CicReport cicReport;

    @Mock
    private ResponseObject<CicReport> responseObject;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private Map<String, String> companyAccountsLinks;

    @Mock
    private Map<String, String> cicReportLinks;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VAR = "companyAccountId";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String CIC_REPORT_SELF_LINK = "cicReportSelfLink";

    @BeforeEach
    void setUp () {
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(COMPANY_ACCOUNTS_ID_PATH_VAR, COMPANY_ACCOUNTS_ID);

        doReturn(pathVariables).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    @Test
    @DisplayName("Cic report interceptor - success")
    void cicReportInterceptorSuccess() throws DataException {
        when(cicReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(cicReport);

        doReturn(companyAccount).when(request).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC_REPORT.getLink())).thenReturn(CIC_REPORT_SELF_LINK);

        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(BasicLinkType.SELF.getLink())).thenReturn(CIC_REPORT_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertTrue(preHandle);

        verify(request).setAttribute(AttributeName.CIC_REPORT.getValue(), cicReport);
    }

    @Test
    @DisplayName("Cic report interceptor - cic report not found")
    void cicReportInterceptorCicReportNotFound() throws DataException {
        when(cicReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Cic report interceptor - parent link not found")
    void cicReportInterceptorParentLinkNotFound() throws DataException {
        when(cicReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(cicReport);

        doReturn(companyAccount).when(request).getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC_REPORT.getLink())).thenReturn(null);

        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(BasicLinkType.SELF.getLink())).thenReturn(CIC_REPORT_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Cic report interceptor - data exception")
    void cicReportInterceptorDataException() throws DataException {
        when(cicReportService.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
