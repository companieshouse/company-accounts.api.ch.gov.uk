package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
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
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodControllerTest {

    public static final String X_REQUEST_ID = "X-Request-Id";
    public static final String TEST = "test";

    @Mock
    private HttpServletRequest request;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private PreviousPeriodController previousPeriodController;

    @BeforeEach
    public void setUp() {
        when(request.getHeader(X_REQUEST_ID)).thenReturn(TEST);
        doReturn(transaction).when(request)
            .getAttribute(AttributeName.TRANSACTION.getValue());
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

        ResponseEntity response = previousPeriodController
            .create(previousPeriod, request);

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
            .create(previousPeriod, request);

        verify(previousPeriodService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).map(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

    }


}
