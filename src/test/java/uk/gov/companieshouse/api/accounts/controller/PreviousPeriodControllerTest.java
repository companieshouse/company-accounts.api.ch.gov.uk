package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodControllerTest {

    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String TRANSACTION_STRING = "transaction";
    public static final String TEST = "test";
    public static final String CREATE = "create";
    public static final String FIND = "find";
    public static final String SELF = "self";

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

    @BeforeEach
    public void setUp() {
        when(request.getAttribute(TRANSACTION_STRING)).thenReturn(transaction);
        when(request.getHeader(X_REQUEST_ID)).thenReturn(TEST);
        doReturn(transaction).when(request)
            .getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn(companyAccountEntity).when(request)
            .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        doReturn("12345").when(companyAccountEntity).getId();

    }

    @Test
    @DisplayName("Tests the successful creation of a previous period resource")
    void canCreatePreviousPeriod() throws DataException {

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
            previousPeriod);
        doReturn(responseObject).when(previousPeriodService)
            .create(any(PreviousPeriod.class), any(Transaction.class), anyString(), anyString());
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getValidationErrorData()))
            .thenReturn(responseEntity);
        doReturn(responseObject).when(previousPeriodService).findById(CREATE, TEST);
        doReturn(new ResponseObject(ResponseStatus.FOUND,
            previousPeriod)).when(previousPeriodService).findById(FIND, TEST);
        doReturn("123456").when(transaction).getCompanyNumber();
        doReturn(links).when(smallFull).getLinks();
        doReturn("7890").when(links).get(SELF);
        ResponseEntity response = previousPeriodController.create(previousPeriod, bindingResult, request);

        verify(apiResponseMapper, times(1)).map(any(), any(), any());

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(previousPeriod, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create previous period")
    void createPreviousPeriodError() throws DataException {

        DataException exception = new DataException("string");

        when(previousPeriodService.create(any(), any(), any(), any())).thenThrow(exception);

        when(apiResponseMapper.map(exception))
            .thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
        ResponseEntity response = previousPeriodController
            .create(previousPeriod, bindingResult, request);

        verify(previousPeriodService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).map(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }
    
    @Test
    @DisplayName("Test correct response when binding result has an error")

    public void badRequestWhenValidationFails() throws DataException{
        

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(any(), any())).thenReturn(errors);
        when(errors.hasErrors()).thenReturn(true);
        
        ResponseEntity<?> response = previousPeriodController.create(previousPeriod, bindingResult, request);

        assertTrue(bindingResult.hasErrors());
        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(any(), any());
        verify(previousPeriodValidator, times(1)).validatePreviousPeriod(any(), any());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }   
}
