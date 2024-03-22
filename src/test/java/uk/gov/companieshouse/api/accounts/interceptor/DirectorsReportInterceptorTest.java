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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class DirectorsReportInterceptorTest {

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @InjectMocks
    private DirectorsReportInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Transaction transaction;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private ResponseObject responseObject;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> smallFullLinks;

    @Mock
    private Map<String, String> directorsReportLinks;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VAR = "companyAccountId";
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String DIRECTORS_REPORT_SELF_LINK = "directorsReportSelfLink";

    @BeforeEach
    void setUp () {

        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put(COMPANY_ACCOUNTS_ID_PATH_VAR, COMPANY_ACCOUNTS_ID);

        doReturn(pathVariables).when(request).getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    }

    @Test
    @DisplayName("Directors report interceptor - success")
    void directorsReportInterceptorSuccess() throws DataException {

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(directorsReport);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.DIRECTORS_REPORT.getLink())).thenReturn(
                DIRECTORS_REPORT_SELF_LINK);

        when(directorsReport.getLinks()).thenReturn(directorsReportLinks);
        when(directorsReportLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                DIRECTORS_REPORT_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertTrue(preHandle);

        verify(request).setAttribute(AttributeName.DIRECTORS_REPORT.getValue(), directorsReport);
    }

    @Test
    @DisplayName("Directors report interceptor - directors report not found")
    void directorsReportInterceptorDirectorsReportNotFound() throws DataException {

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.NOT_FOUND);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("Directors report interceptor - parent link not found")
    void directorsReportInterceptorParentLinkNotFound() throws DataException {

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);
        when(responseObject.getStatus()).thenReturn(ResponseStatus.FOUND);
        when(responseObject.getData()).thenReturn(directorsReport);

        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.DIRECTORS_REPORT.getLink())).thenReturn(null);

        when(directorsReport.getLinks()).thenReturn(directorsReportLinks);
        when(directorsReportLinks.get(BasicLinkType.SELF.getLink())).thenReturn(
                DIRECTORS_REPORT_SELF_LINK);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Directors report interceptor - data exception")
    void directorsReportInterceptorDataException() throws DataException {

        when(directorsReportService.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        boolean preHandle = interceptor.preHandle(request, response, new Object());

        assertFalse(preHandle);

        verify(response).setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
