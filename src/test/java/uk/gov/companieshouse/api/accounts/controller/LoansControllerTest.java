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
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.LoanServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class LoansControllerTest {

    @Mock
    private LoanServiceImpl loanService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private Loan loanRest;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private BindingResult bindingResult;
    
    @Mock
    private ErrorMapper errorMapper;
    
    @Mock
    private Errors errors;
    
    @Mock
    private Map<String, String> loans;

    @InjectMocks
    private LoansController controller;

    private static final String LOANS_ID = "loansId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private static final String LOANS_LINK = "loansLink";

    @Test
    @DisplayName("Tests the successful creation of a Loan")
    void createLoanSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Loan> responseObject = new ResponseObject<>(ResponseStatus.CREATED, loanRest);
        when(loanService.create(loanRest, transaction, LOANS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(loanRest, bindingResult, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(loanRest, response.getBody());

        verify(loanService, times(1))
                .create(loanRest, transaction, LOANS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());

    }

    @Test
    @DisplayName("Tests the creation of a Loan where the service throws a data exception")
    void createLoanAndServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(loanService)
                .create(loanRest, transaction, LOANS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(loanRest, bindingResult, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1))
                .create(loanRest, transaction, LOANS_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }
    
    @Test
    @DisplayName("Tests the creation of a Loan where the controller returns a bad request binding error for invalid length")
    void createLoanReturnBadRequestForBindingErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);
        
        ResponseEntity response =
                controller.create(loanRest, bindingResult, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful retrieval of a Loan")
    void getLoanSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Loan> responseObject = new ResponseObject<>(ResponseStatus.FOUND, loanRest);
        when(loanService.find(LOANS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.get(LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(loanRest, response.getBody());

        verify(loanService, times(1))
                .find(LOANS_ID, request);
        verify(apiResponseMapper, times(1))
                .mapGetResponse(responseObject.getData(), request);
    }

    @Test
    @DisplayName("Tests the retrieval of a Loan when the service throws a DataException")
    void getLoanServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(loanService).find(LOANS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.get(LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1))
                .find(LOANS_ID, request);
        verify(apiResponseMapper, never()).mapGetResponse(any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of all Loans")
    void getAllLoansSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        Loan[] loans = new Loan[0];
        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, loans);
        when(loanService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getDataForMultipleResources());
        when(apiResponseMapper.mapGetResponseForMultipleResources(responseObject.getDataForMultipleResources(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(loans, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of all Loans when the service throws a DataException")
    void getAllLoansServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(loanService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a Loan resource")
    void updateLoanSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(anyString())).thenReturn(loansToDirectors).thenReturn(transaction);

        when(loansToDirectors.getLoans()).thenReturn(loans);
        when(loans.get(LOANS_ID)).thenReturn(LOANS_LINK);

        ResponseObject<Loan> responseObject = new ResponseObject<>(ResponseStatus.UPDATED, loanRest);
        when(loanService.update(loanRest, transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(loanRest, bindingResult, COMPANY_ACCOUNT_ID, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1))
                .update(loanRest, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the update of a Loan when the Loan ID doesnt exist")
    void updateDirectorResourceWhenIdIsNull() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(anyString())).thenReturn(loansToDirectors).thenReturn(transaction);

        when(loansToDirectors.getLoans()).thenReturn(loans);
        when(loans.get(LOANS_ID)).thenReturn(null);

        ResponseEntity response =
                controller.update(loanRest, bindingResult, COMPANY_ACCOUNT_ID, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the update of a Loans resource where the service throws a data exception")
    void updateLoansServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(anyString())).thenReturn(loansToDirectors).thenReturn(transaction);

        when(loansToDirectors.getLoans()).thenReturn(loans);
        when(loans.get(LOANS_ID)).thenReturn(LOANS_LINK);

        doThrow(new DataException("")).when(loanService)
                .update(loanRest, transaction, COMPANY_ACCOUNT_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(loanRest, bindingResult, COMPANY_ACCOUNT_ID, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1))
                .update(loanRest, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }
    
    @Test
    @DisplayName("Tests the update of a Loan where the controller returns a bad request binding error for invalid length")
    void updateLoanReturnBadRequestForBindingErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);
        
        ResponseEntity response =
                controller.update(loanRest, bindingResult, COMPANY_ACCOUNT_ID, LOANS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful deletion of a Loan resource")
    void deleteLoanSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(loanService.delete(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the deletion of a Loan resource where the service throws a data exception")
    void deleteLoanServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(loanService).delete(COMPANY_ACCOUNT_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(loanService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }
}
