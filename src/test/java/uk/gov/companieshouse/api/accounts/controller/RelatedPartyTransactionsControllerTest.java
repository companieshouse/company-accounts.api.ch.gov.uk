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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.service.impl.RelatedPartyTransactionsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelatedPartyTransactionsControllerTest {
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private RelatedPartyTransactions relatedPartyTransactions;

    @Mock
    private RelatedPartyTransactionsServiceImpl relatedPartyTransactionsService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private RelatedPartyTransactionsController relatedPartyTransactionsController;

    @Test
    @DisplayName("Related party transactions create - successful")
    void createRelatedPartyTransactionsSuccessful() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<RelatedPartyTransactions> responseObject = new ResponseObject<>(ResponseStatus.CREATED,
                relatedPartyTransactions);

        when(relatedPartyTransactionsService.create(relatedPartyTransactions, transaction,
                COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity<RelatedPartyTransactions> responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());

        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> response =
                relatedPartyTransactionsController.create(relatedPartyTransactions, COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(relatedPartyTransactions, responseEntity.getBody());
    }

    @Test
    @DisplayName("Related party transactions create - Throws exception")
    void createRelatedPartyTransactionsThrowsException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(relatedPartyTransactionsService.create(relatedPartyTransactions, transaction,
                COMPANY_ACCOUNT_ID, request)).thenThrow(DataException.class);

        ResponseEntity<RelatedPartyTransactions> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity<?> response =
                relatedPartyTransactionsController.create(relatedPartyTransactions, COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get related party transactions - success")
    void getRelatedPartyTransactionsSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<RelatedPartyTransactions> responseObject = new ResponseObject<>(ResponseStatus.FOUND,
                relatedPartyTransactions);
        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity<RelatedPartyTransactions> responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());

        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request)).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = relatedPartyTransactionsController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(relatedPartyTransactions, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get related party transactions - Throws exception")
    void getRelatedPartyTransactionsThrowsException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(relatedPartyTransactionsService.find(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity<RelatedPartyTransactions> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = relatedPartyTransactionsController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete related party transactions - success")
    void deleteRelatedPartyTransactionsSuccess() throws DataException {
        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject<RelatedPartyTransactions> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                relatedPartyTransactions);

        when(relatedPartyTransactionsService.delete(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<RelatedPartyTransactions> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                relatedPartyTransactionsController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete related party transactions - Throws exception")
    void deleteRelatedPartyTransactionsThrowsException() throws DataException {
        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(relatedPartyTransactionsService.delete(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity<RelatedPartyTransactions> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = relatedPartyTransactionsController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
