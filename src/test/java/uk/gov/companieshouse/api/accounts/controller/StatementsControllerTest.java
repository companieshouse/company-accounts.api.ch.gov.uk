package uk.gov.companieshouse.api.accounts.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.service.impl.StatementService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class StatementsControllerTest {

    private static final String STATEMENT_ID = "abcdef";
    private static final String COMPANY_ACCOUNTS_ID = "123123";

    @Mock
    private Statement statementMock;
    @Mock
    private StatementService statementServiceMock;
    @Mock
    private Transaction transactionMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private ApiResponseMapper apiResponseMapperMock;

    @InjectMocks
    private StatementsController statementsController;

    @Test
    @DisplayName("Tests the successful request to create Statements")
    void shouldCreateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(transactionMock);

        ResponseObject restObjectCreated = createResponseObject(ResponseStatus.CREATED);

        when(statementServiceMock.create(any(Statement.class), any(Transaction.class), anyString(),
            any(HttpServletRequest.class)))
            .thenReturn(restObjectCreated);

        when(apiResponseMapperMock.map(restObjectCreated.getStatus(), restObjectCreated.getData(),
            restObjectCreated.getErrors()))
            .thenReturn(createResponseEntity(HttpStatus.CREATED, restObjectCreated));

        ResponseEntity response =
            statementsController.create(statementMock, "", requestMock);

        assertStatementControllerResponse(response, HttpStatus.CREATED, true);
        verifyStatementServiceCreateCall();
        verifyApiResponseMapperMapCallWhenNoErrors(restObjectCreated);
    }

    @Test
    @DisplayName("Tests the unsuccessful to create Statements")
    void shouldNotCreateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(transactionMock);

        when(statementServiceMock.create(any(Statement.class), any(Transaction.class), anyString(),
            any(HttpServletRequest.class)))
            .thenThrow(new DataException(""));

        when(apiResponseMapperMock.getErrorResponse())
            .thenReturn(createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, null));

        ResponseEntity response =
            statementsController.create(statementMock, "", requestMock);

        assertStatementControllerResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, false);
        verifyStatementServiceCreateCall();
        verify(apiResponseMapperMock, times(1)).getErrorResponse();
    }


    @Test
    @DisplayName("Tests the successful request to Update Statements")
    void shouldUpdateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(transactionMock);

        ResponseObject restObjectUpdated = createResponseObject(ResponseStatus.UPDATED);

        when(statementServiceMock.update(any(Statement.class), any(Transaction.class), anyString(),
            any(HttpServletRequest.class)))
            .thenReturn(restObjectUpdated);

        when(apiResponseMapperMock.map(restObjectUpdated.getStatus(), restObjectUpdated.getData(),
            restObjectUpdated.getErrors()))
            .thenReturn(createResponseEntity(HttpStatus.NO_CONTENT, restObjectUpdated));

        ResponseEntity response =
            statementsController.update(statementMock, "", requestMock);

        assertStatementControllerResponse(response, HttpStatus.NO_CONTENT, true);
        verifyStatementServiceUpdate();
        verifyApiResponseMapperMapCallWhenNoErrors(restObjectUpdated);
    }

    @Test
    @DisplayName("Tests the unsuccessful request to Update Statements")
    void shouldNotUpdateStatement() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(transactionMock);

        when(statementServiceMock.update(any(Statement.class), any(Transaction.class), anyString(),
            any(HttpServletRequest.class)))
            .thenThrow(new DataException(""));

        when(apiResponseMapperMock.getErrorResponse())
            .thenReturn(createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, null));

        ResponseEntity response =
            statementsController.update(statementMock, "", requestMock);

        assertStatementControllerResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, false);
        verifyStatementServiceUpdate();
        verify(apiResponseMapperMock, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful request to get Statements")
    void shouldGetStatements() throws DataException {
        when(statementServiceMock.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(STATEMENT_ID);

        ResponseObject restObjectFound = createResponseObject(ResponseStatus.FOUND);

        when(statementServiceMock.findById(STATEMENT_ID, requestMock))
            .thenReturn(restObjectFound);

        when(apiResponseMapperMock.mapGetResponse(restObjectFound.getData(), requestMock))
            .thenReturn(createResponseEntity(HttpStatus.OK, restObjectFound));

        ResponseEntity response =
            statementsController.get(COMPANY_ACCOUNTS_ID, requestMock);

        assertStatementControllerResponse(response, HttpStatus.OK, true);
        verifyStatementServiceGenerateAndFind(STATEMENT_ID);
        verifyApiResponseMapperMapGetResponseCall(restObjectFound);
    }

    @Test
    @DisplayName("Tests the unsuccessful request to get Statements as statement id not found")
    void shouldNotGetStatementsStatementIdNotFound() throws DataException {
        when(statementServiceMock.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(STATEMENT_ID);

        ResponseObject restObjectNotFound = createResponseObject(ResponseStatus.NOT_FOUND);

        when(statementServiceMock.findById(STATEMENT_ID, requestMock))
            .thenReturn(restObjectNotFound);

        when(apiResponseMapperMock.mapGetResponse(restObjectNotFound.getData(), requestMock))
            .thenReturn(createResponseEntity(HttpStatus.NOT_FOUND, restObjectNotFound));

        ResponseEntity response =
            statementsController.get(COMPANY_ACCOUNTS_ID, requestMock);

        assertStatementControllerResponse(response, HttpStatus.NOT_FOUND, true);
        verifyStatementServiceGenerateAndFind(STATEMENT_ID);
        verifyApiResponseMapperMapGetResponseCall(restObjectNotFound);
    }

    @Test
    @DisplayName("Tests the unsuccessful request to get Statements as exception occurs")
    void shouldNotGetStatementsStatementAsExceptionOccurs() throws DataException {
        when(requestMock.getAttribute(anyString())).thenReturn(transactionMock);

        when(statementServiceMock.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(STATEMENT_ID);

        when(statementServiceMock.findById(STATEMENT_ID, requestMock))
            .thenThrow(new DataException(""));

        when(apiResponseMapperMock.getErrorResponse())
            .thenReturn(createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, null));

        ResponseEntity response =
            statementsController.get(COMPANY_ACCOUNTS_ID, requestMock);

        assertStatementControllerResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, false);
        verifyStatementServiceGenerateAndFind(STATEMENT_ID);
        verify(apiResponseMapperMock, times(1)).getErrorResponse();
    }


    /**
     * Asserts the response is not null, it is equal to the status passed and the body match the
     * statementMock (no change in the object occurs). Body is assessed if flag is passed in.
     *
     * @param response
     * @param httpResponseStatus
     * @param assertResponseBody
     */
    private void assertStatementControllerResponse(ResponseEntity response,
        HttpStatus httpResponseStatus, boolean assertResponseBody) {

        assertNotNull(response);
        assertEquals(httpResponseStatus, response.getStatusCode());

        if (assertResponseBody) {
            assertEquals(statementMock, response.getBody());
        }
    }

    /**
     * Verify the apiResponseMapper.map() is called 1 time when no error occurs.
     *
     * @param responseObject
     */
    private void verifyApiResponseMapperMapCallWhenNoErrors(ResponseObject responseObject) {
        verify(apiResponseMapperMock, times(1))
            .map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors());
    }

    /**
     * Verify the apiResponseMapper.map() is called 1 time when no error occurs.
     *
     * @param responseObject
     */
    private void verifyApiResponseMapperMapGetResponseCall(ResponseObject responseObject) {
        verify(apiResponseMapperMock, times(1))
            .mapGetResponse(responseObject.getData(), requestMock);
    }

    /**
     * Verify the statementService.create is called 1 time.
     */
    private void verifyStatementServiceCreateCall() throws DataException {
        verify(statementServiceMock, times(1))
            .create(any(Statement.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
    }


    /**
     * Verify statementService.update is called 1 time.
     *
     * @throws DataException
     */
    private void verifyStatementServiceUpdate() throws DataException {
        verify(statementServiceMock, times(1))
            .update(any(Statement.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
    }

    /**
     * Verify statementService.update is called 1 time.
     *
     * @throws DataException
     */
    private void verifyStatementServiceGenerateAndFind(String a) throws DataException {

        verify(statementServiceMock, times(1))
            .generateID(COMPANY_ACCOUNTS_ID);

        verify(statementServiceMock, times(1))
            .findById(STATEMENT_ID, requestMock);
    }

    /**
     * Creates a response entity with the status passed in and if body exists, it will added to the
     * body.
     *
     * @param httpStatus
     * @param responseObjectObject
     * @return
     */
    private ResponseEntity createResponseEntity(HttpStatus httpStatus,
        ResponseObject responseObjectObject) {

        if (responseObjectObject != null) {
            return ResponseEntity.status(httpStatus).body(responseObjectObject.getData());
        }

        return ResponseEntity.status(httpStatus).build();
    }

    private ResponseObject<Statement> createResponseObject(ResponseStatus responseStatus) {
        return new ResponseObject(responseStatus, statementMock);
    }
}
