package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.Cic34Report;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.Cic34ReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class Cic34ReportControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private Cic34Report cic34Report;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Cic34ReportService service;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> companyAccountsLinks;

    @InjectMocks
    private Cic34ReportController controller;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String CIC34_REPORT_LINK = "cic34ReportLink";

    @Test
    @DisplayName("Create CIC34 report - success path")
    void createCIC34ReportSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, cic34Report);
        when(service.create(cic34Report, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, returnedResponse.getStatusCode());
        assertEquals(cic34Report, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create CIC34 report - binding result errors")
    void createCIC34ReportBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                controller.create(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create CIC34 report - data exception")
    void createCIC34ReportDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.create(cic34Report, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get CIC34 report - success path")
    void getCIC34ReportSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, cic34Report);
        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, returnedResponse.getStatusCode());
        assertEquals(cic34Report, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get CIC34 report - data exception")
    void getCIC34ReportDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC34 report - success path")
    void updateCIC34ReportSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(companyAccount).thenReturn(transaction);
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC34_REPORT.getLink()))
                .thenReturn(CIC34_REPORT_LINK);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, cic34Report);
        when(service.update(cic34Report, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC34 report - not found")
    void updateCIC34ReportNotFound() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC34_REPORT.getLink()))
                .thenReturn(null);

        ResponseEntity returnedResponse =
                controller.update(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NOT_FOUND, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC34 report - binding result errors")
    void updateCIC34ReportBindingResultErrors() {

        when(request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue())).thenReturn(companyAccount);
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC34_REPORT.getLink()))
                .thenReturn(CIC34_REPORT_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                controller.update(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC34 report - data exception")
    void updateCIC34ReportDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(companyAccount).thenReturn(transaction);
        when(companyAccount.getLinks()).thenReturn(companyAccountsLinks);
        when(companyAccountsLinks.get(CompanyAccountLinkType.CIC34_REPORT.getLink()))
                .thenReturn(CIC34_REPORT_LINK);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(service.update(cic34Report, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(cic34Report, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete CIC34 report - success path")
    void deleteCIC34ReportSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete CIC34 report - data exception")
    void deleteCIC34ReportDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }
}
