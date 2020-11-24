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
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Statements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.StatementsService;
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
class DirectorStatementsControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String STATEMENTS_LINK = "statementsLink";

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Statements statements;

    @Mock
    private StatementsService statementsService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private Map<String, String> statementsLink;

    @Mock
    private Map<String, String> directorsReportLink;

    @InjectMocks
    private DirectorStatementsController statementsController;

    @Test
    @DisplayName("Statements resource created successfully")
    void createStatementsResourceSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Statements> responseObject = new ResponseObject<>(ResponseStatus.CREATED,
                statements);
        when(statementsService.create(statements, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.create(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(statements, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create statements has failed - data exception thrown")
    void createStatementsResourceDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(statementsService.create(statements, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.create(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create statements resource - has binding errors")
    void createStatementsResourceBindingErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                statementsController.create(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update statements resource - no statements link")
    void updateStatementsResourceNoStatementsLink() {

        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);
        when(directorsReport.getLinks()).thenReturn(directorsReportLink);
        when(directorsReportLink.get(DirectorsReportLinkType.STATEMENTS.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
                statementsController.update(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update statements resource - has binding errors")
    void updateStatementsResourceBindingErrors() {

        mockTransactionAndLinks();

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                statementsController.update(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update statements resource - success")
    void updateStatementsResourceSuccess() throws DataException {

        mockTransactionAndLinks();


        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject<Statements> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                statements);
        when(statementsService.update(statements, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.update(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update statements resource - data exception thrown")
    void updateStatementsResourceDataException() throws DataException {

        mockTransactionAndLinks();

        when(bindingResult.hasErrors()).thenReturn(false);

        when(statementsService.update(statements, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.update(statements, bindingResult,
                        COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get statements resource - success")
    void getStatementsResourceSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Statements> responseObject = new ResponseObject<>(ResponseStatus.FOUND,
                statements);
        when(statementsService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(statements, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get statements resource - data exception thrown")
    void getStatementsResourceDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(statementsService.find(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = statementsController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete statements resource - success")
    void deleteStatementsResourceSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject<Statements> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                statements);

        when(statementsService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                statementsController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete statements resource - data exception thrown")
    void deleteStatementsResourceDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(statementsService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = statementsController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);
        when(directorsReport.getLinks()).thenReturn(directorsReportLink);
        when(directorsReportLink.get(DirectorsReportLinkType.STATEMENTS.getLink())).thenReturn(STATEMENTS_LINK);
    }
} 
