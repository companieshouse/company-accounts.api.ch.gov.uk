package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.service.impl.CicReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CicReportControllerTest {
    @Mock
    private CicReportService service;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private CicReportController controller;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CicReport cicReport;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Test
    @DisplayName("Create cic report - success")
    void createCicReportSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<CicReport> responseObject = new ResponseObject<>(ResponseStatus.CREATED, cicReport);
        when(service.create(cicReport, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<CicReport> responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                controller.create(cicReport, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, returnedResponse.getStatusCode());
        assertEquals(cicReport, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create cic report - data exception")
    void createCicReportDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.create(cicReport, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                controller.create(cicReport, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get cic report - success")
    void getCicReportSuccess() throws DataException {
        ResponseObject<CicReport> responseObject = new ResponseObject<>(ResponseStatus.FOUND, cicReport);
        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<CicReport> responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, returnedResponse.getStatusCode());
        assertEquals(cicReport, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get cic report - data exception")
    void getCicReportDataException() throws DataException {
        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete cic report - success")
    void deleteCicReportSuccess() throws DataException {
        ResponseObject<CicReport> responseObject = new ResponseObject<>(ResponseStatus.UPDATED);
        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete cic report - data exception")
    void deleteCicReportDataException() throws DataException {
        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }
}
