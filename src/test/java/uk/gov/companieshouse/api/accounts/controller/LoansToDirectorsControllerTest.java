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
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.service.impl.LoansToDirectorsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoansToDirectorsControllerTest {

    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private LoansToDirectors loansToDirectors;

    @Mock
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private LoansToDirectorsController loansToDirectorsController;

    @Test
    @DisplayName("Loans to directors create - successful")
    void createLoansToDirectorsSuccessful() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
                loansToDirectors);

        when(loansToDirectorsService.create(loansToDirectors, transaction,
                COMPANY_ACCOUNT_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());

        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                loansToDirectorsController.create(loansToDirectors, COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(loansToDirectors, responseEntity.getBody());
    }

    @Test
    @DisplayName("Loans to directors create - Throws exception")
    void createLoansToDirectorsThrowsException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(loansToDirectorsService.create(loansToDirectors, transaction,
                COMPANY_ACCOUNT_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity response =
                loansToDirectorsController.create(loansToDirectors, COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get loans to directors - success")
    void getLoansToDirectorsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND,
                loansToDirectors);
        when(loansToDirectorsService.find(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());

        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                loansToDirectorsController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(loansToDirectors, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get loans to directors - Throws exception")
    void getLoansToDirectorsThrowsException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        DataException dataException = new DataException("");
        when(loansToDirectorsService.find(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = loansToDirectorsController.get(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete loans to directors - success")
    void deleteLoansToDirectorsSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                loansToDirectors);

        when(loansToDirectorsService.delete(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                loansToDirectorsController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete loans to directors - Throws exception")
    void deleteLoansToDirectorsThrowsException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(loansToDirectorsService.delete(COMPANY_ACCOUNT_ID, request))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = loansToDirectorsController.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}
