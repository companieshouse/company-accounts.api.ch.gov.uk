package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirectorsReportControllerTest {

    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private DirectorsReportService directorsReportService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> smallFullLinks;

    @InjectMocks
    private DirectorsReportController directorsReportController;

    @Test
    @DisplayName("Directors report resource created successfully")
    void createDirectorsReportResource() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
                directorsReport);
        when(directorsReportService.create(directorsReport, transaction,
                COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                directorsReportController.create(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(directorsReport, responseEntity.getBody());
    }

    @Test
    @DisplayName("Directors report create resource throws data exception")
    void createDirectorsReportThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(directorsReportService.create(directorsReport, transaction,
                COMPANY_ACCOUNT_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                directorsReportController.create(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create directors report resource - has binding errors")
    void createDirectorsReportBindingErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                directorsReportController.create(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update directors report - no small full link")
    void updateDirectorsReportNoSmallFullLink() {

        when(request.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(smallFull);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.DIRECTORS_REPORT.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
                directorsReportController.update(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update directors report - has binding errors")
    void updateDirectorReportBindingErrors() {

        mockTransactionAndLinks();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                directorsReportController.update(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update directors report - success")
    void updateDirectorsReportSuccess() throws DataException {

        mockTransactionAndLinks();
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                directorsReport);
        when(directorsReportService.update(directorsReport, transaction,
                COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                directorsReportController.update(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update directors report resource - data exception thrown")
    void updateDirectorsReportDataException() throws DataException {

        mockTransactionAndLinks();
        when(bindingResult.hasErrors()).thenReturn(false);

        DataException dataException = new DataException("");
        when(directorsReportService.update(directorsReport, transaction,
                COMPANY_ACCOUNT_ID, request)).thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                directorsReportController.update(directorsReport, bindingResult,
                        COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get directors report resource - success")
    void getDirectorsReportSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND,
                directorsReport);
        when(directorsReportService.find(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                directorsReportController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(directorsReport, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get directors report resource - data exception thrown")
    void getDirectorsReportDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(directorsReportService.find(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = directorsReportController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete directors report resource - success")
    void deleteDirectorsReportSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                directorsReport);

        when(directorsReportService.delete(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                directorsReportController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete directors report resource - data exception thrown")
    void deleteDirectorsReportDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(directorsReportService.delete(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = directorsReportController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(smallFullLinks);
        when(smallFullLinks.get(SmallFullLinkType.DIRECTORS_REPORT.getLink())).thenReturn("");
    }
}
