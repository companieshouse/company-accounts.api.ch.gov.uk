package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.validation.PreviousPeriodValidator;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodControllerTest {

    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String TRANSACTION_STRING = "transaction";
    public static final String TEST = "test";
    public static final String CREATE = "create";
    public static final String FIND = "find";
    public static final String SELF = "self";
    public static final String COMPANY_ACCOUNT_ID = "12345";

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult result;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private PreviousPeriodValidator previousPeriodValidator;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private Errors errors;

    @Mock
    private SmallFull smallFull;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private PreviousPeriodController previousPeriodController;

    @Test
    @DisplayName("Tests the successful creation of a previous period resource")
    void canCreatePreviousPeriod() throws DataException {
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());
        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, previousPeriod);
        doReturn(responseObject).when(previousPeriodService).create(any(PreviousPeriod.class), any(Transaction.class), anyString(), anyString());
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(),responseObject.getData(), responseObject.getValidationErrorData())).thenReturn(responseEntity);

        when(previousPeriodValidator.validatePreviousPeriod(any())).thenReturn(errors);

        ResponseEntity response = previousPeriodController.create(previousPeriod, bindingResult, "", request);

        verify(apiResponseMapper, times(1)).map(any(), any(), any());

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(previousPeriod, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create previous period")
    void createPreviousPeriodError() throws DataException {

        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        DataException exception = new DataException("string");

        when(previousPeriodValidator.validatePreviousPeriod(any())).thenReturn(errors);

        when(previousPeriodService.create(any(), any(), any(), any())).thenThrow(exception);

        when(apiResponseMapper.map(exception)).thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
        ResponseEntity response = previousPeriodController.create(previousPeriod, bindingResult,"", request);

        verify(previousPeriodService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).map(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test correct response when binding result has an error")

    public void badRequestWhenBindingResultHasErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = previousPeriodController.create(previousPeriod, bindingResult,
                COMPANY_ACCOUNT_ID, request);

        assertTrue(bindingResult.hasErrors());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the successful retrieval of a previous period resource")
    public void canRetrievePreviousPeriod() throws DataException {
        doReturn("find").when(previousPeriodService).generateID("123456");
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());
        when(request.getHeader("X-Request-Id")).thenReturn("REQUEST_ID");

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(previousPeriod);

        when(previousPeriodService.findById(anyString(), anyString())).thenReturn(new ResponseObject(ResponseStatus.FOUND, previousPeriod));
        when(apiResponseMapper.mapGetResponse(previousPeriod, request)).thenReturn(responseEntity);

        ResponseEntity response = previousPeriodController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(previousPeriod, response.getBody());
    }

    @Test
    @DisplayName("Test the unsuccessful retrieval of a previous period resource")
    public void canRetrievePreviousPeriodFailed() throws DataException {
        doReturn("find").when(previousPeriodService).generateID("123456");
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());
        when(request.getHeader("X-Request-Id")).thenReturn("REQUEST_ID");

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        when(previousPeriodService.findById(anyString(), anyString())).thenThrow(new DataException("error"));
        when(apiResponseMapper.map(any(DataException.class))).thenReturn(responseEntity);

        ResponseEntity response = previousPeriodController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test correct response when validator fails")
    public void badRequestWhenValidatorFails() {
        when(previousPeriodValidator.validatePreviousPeriod(any())).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);

        ResponseEntity<?> response = previousPeriodController.create(previousPeriod, bindingResult,COMPANY_ACCOUNT_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}