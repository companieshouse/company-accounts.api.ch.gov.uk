package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.RptTransactionServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RptTransactionsControllerTest {
    private static final String RPT_TRANSACTIONS_ID = "rptTransactionsId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private static final String RPT_TRANSACTIONS_LINK = "rptTransactionsLink";

    @Mock
    private RptTransactionServiceImpl rptTransactionService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private RptTransaction rptTransaction;

    @Mock
    private RelatedPartyTransactions relatedPartyTransactions;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> rptTransactions;
    
    @InjectMocks
    private RptTransactionsController controller;

    @Test
    @DisplayName("Tests the successful creation of a RptTransaction")
    void createRptTransactionSuccess() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<RptTransaction> responseObject = new ResponseObject<RptTransaction>(ResponseStatus.CREATED, rptTransaction);
        when(rptTransactionService.create(rptTransaction, transaction, RPT_TRANSACTIONS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<RptTransaction> responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> response =
                controller.create(rptTransaction, bindingResult, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(rptTransaction, response.getBody());

        verify(rptTransactionService, times(1))
                .create(rptTransaction, transaction, RPT_TRANSACTIONS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());

    }

    @Test
    @DisplayName("Tests the creation of an RptTransaction where the controller returns a bad request binding error for invalid length")
    void createRptTransactionReturnBadRequestForBindingErrors() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity<?> response = controller.create(rptTransaction, bindingResult, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the creation of an RptTransaction where the service throws a data exception")
    void createRptTransactionAndServiceThrowsDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(rptTransactionService)
                .create(rptTransaction, transaction, RPT_TRANSACTIONS_ID, request);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> response = controller.create(rptTransaction, bindingResult, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1))
                .create(rptTransaction, transaction, RPT_TRANSACTIONS_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of an RptTransaction")
    void getRptTransactionSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<RptTransaction> responseObject = new ResponseObject<>(ResponseStatus.FOUND, rptTransaction);
        when(rptTransactionService.find(RPT_TRANSACTIONS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<RptTransaction> responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity<?> response = controller.get(RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(rptTransaction, response.getBody());

        verify(rptTransactionService, times(1))
                .find(RPT_TRANSACTIONS_ID, request);
        verify(apiResponseMapper, times(1))
                .mapGetResponse(responseObject.getData(), request);
    }

    @Test
    @DisplayName("Tests the retrieval of an RptTransaction when the service throws a DataException")
    void getRptTransactionServiceThrowsDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(rptTransactionService).find(RPT_TRANSACTIONS_ID, request);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> response = controller.get(RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1))
                .find(RPT_TRANSACTIONS_ID, request);
        verify(apiResponseMapper, never()).mapGetResponse(any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of all RptTransactions")
    void getAllRptTransactionsSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        RptTransaction[] rptTransactions = new RptTransaction[0];
        ResponseObject<RptTransaction> responseObject = new ResponseObject<>(ResponseStatus.FOUND, rptTransactions);
        when(rptTransactionService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<RptTransaction[]> responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getDataForMultipleResources());
        when(apiResponseMapper.mapGetResponseForMultipleResources(responseObject.getDataForMultipleResources(), request))
                .thenReturn(responseEntity);

        ResponseEntity<?> response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(rptTransactions, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of all RptTransactions when the service throws a DataException")
    void getAllRptTransactionsServiceThrowsDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(rptTransactionService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of an RptTransaction resource")
    void updateRptTransactionSuccess() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(anyString())).thenReturn(relatedPartyTransactions).thenReturn(transaction);

        when(relatedPartyTransactions.getTransactions()).thenReturn(rptTransactions);
        when(rptTransactions.get(RPT_TRANSACTIONS_ID)).thenReturn(RPT_TRANSACTIONS_LINK);

        ResponseObject<RptTransaction> responseObject = new ResponseObject<>(ResponseStatus.UPDATED, rptTransaction);
        when(rptTransactionService.update(rptTransaction, transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> response =
                controller.update(rptTransaction, bindingResult, COMPANY_ACCOUNT_ID, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1))
                .update(rptTransaction, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }


    @Test
    @DisplayName("Tests the updating of an RptTransaction where the controller returns a bad request binding error for invalid length")
    void updateRptTransactionReturnBadRequestForBindingErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity<?> response =
                controller.update(rptTransaction, bindingResult, COMPANY_ACCOUNT_ID, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the update of an RptTransaction when the RptTransaction ID doesn't exist")
    void updateRptTransactionResourceWhenIdIsNull() {
        when(request.getAttribute(anyString())).thenReturn(relatedPartyTransactions).thenReturn(transaction);

        when(relatedPartyTransactions.getTransactions()).thenReturn(rptTransactions);
        when(rptTransactions.get(RPT_TRANSACTIONS_ID)).thenReturn(null);

        ResponseEntity<?> response =
                controller.update(rptTransaction, bindingResult, COMPANY_ACCOUNT_ID, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the update of an RptTransaction resource where the service throws a data exception")
    void updateRptTransactionServiceThrowsDataException() throws DataException {
        when(request.getAttribute(anyString())).thenReturn(relatedPartyTransactions).thenReturn(transaction);

        when(relatedPartyTransactions.getTransactions()).thenReturn(rptTransactions);
        when(rptTransactions.get(RPT_TRANSACTIONS_ID)).thenReturn(RPT_TRANSACTIONS_LINK);

        doThrow(new DataException("")).when(rptTransactionService)
                .update(rptTransaction, transaction, COMPANY_ACCOUNT_ID, request);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> response =
                controller.update(rptTransaction, bindingResult, COMPANY_ACCOUNT_ID, RPT_TRANSACTIONS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1))
                .update(rptTransaction, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful deletion of an RptTransaction resource")
    void deleteRptTransactionSuccess() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<RptTransaction> responseObject = new ResponseObject<>(ResponseStatus.UPDATED);
        when(rptTransactionService.delete(COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the deletion of an RptTransaction resource where the service throws a data exception")
    void deleteRptTransactionServiceThrowsDataException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(rptTransactionService).delete(COMPANY_ACCOUNT_ID, request);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(rptTransactionService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }
}