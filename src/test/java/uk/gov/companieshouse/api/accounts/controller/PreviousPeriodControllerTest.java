package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PreviousPeriodControllerTest {

    public static final String COMPANY_ACCOUNT_ID = "12345";

    @Mock
    private HttpServletRequest request;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private Transaction transaction;

    @Mock
    private PreviousPeriodService previousPeriodService;

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

    @InjectMocks
    private PreviousPeriodController previousPeriodController;

    @Test
    @DisplayName("Tests the successful creation of a previous period resource")
    void canCreatePreviousPeriod() throws DataException {
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());
        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, previousPeriod);
        doReturn(responseObject).when(previousPeriodService)
            .create(any(PreviousPeriod.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper
            .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity response = previousPeriodController
            .create(previousPeriod, bindingResult, "", request);

        verify(apiResponseMapper, times(1)).map(any(), any(), any());

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(previousPeriod, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create previous period")
    void createPreviousPeriodError() throws DataException {

        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        when(previousPeriodService.create(any(), any(), any(), any())).thenThrow(new DataException(""));

        when(apiResponseMapper.getErrorResponse())
            .thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));
        ResponseEntity response = previousPeriodController
            .create(previousPeriod, bindingResult, "", request);

        verify(previousPeriodService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test correct response when binding result has an error")
    public void badRequestWhenBindingResultHasErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity<?> response = previousPeriodController.create(previousPeriod, bindingResult,
            COMPANY_ACCOUNT_ID, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the successful retrieval of a previous period resource")
    public void canRetrievePreviousPeriod() throws DataException {
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(previousPeriod);

        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class)))
            .thenReturn(new ResponseObject(ResponseStatus.FOUND, previousPeriod));
        when(apiResponseMapper.mapGetResponse(previousPeriod, request)).thenReturn(responseEntity);

        ResponseEntity response = previousPeriodController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT - Tests the successful update of a previous period resource")
    public void canUpdatePreviousPeriod() throws DataException {
        mockSmallFull();
        mockPreviousPeriodLinkOnSmallFullResource();
        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, previousPeriod);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(),null, responseObject.getErrors()))
                .thenReturn(responseEntity);
        doReturn(responseObject).when(previousPeriodService).update(previousPeriod, null, "12345", request);

        ResponseEntity response = previousPeriodController.update(previousPeriod, bindingResult, "12345", request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT - Tests the unsuccessful update of a previous period resource due to no link to small full resource")
    public void canUpdatePreviousPeriodFail() {
        mockSmallFull();
        when(smallFull.getLinks()).thenReturn(new HashMap<>());
        ResponseEntity response = previousPeriodController.update(previousPeriod, bindingResult, "123456", request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("PUT - Tests the unsuccessful update of a previous period resource due to binding result errors")
    public void canUpdatePreviousPeriodFailBindingResultErrors() {
        mockSmallFull();
        mockPreviousPeriodLinkOnSmallFullResource();
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response = previousPeriodController.update(previousPeriod, bindingResult, "123456", request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the unsuccessful retrieval of a previous period resource")
    public void canRetrievePreviousPeriodFailed() throws DataException {
        doReturn(transaction).when(request).getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(null);
        when(previousPeriodService.find(anyString(), any(HttpServletRequest.class)))
            .thenThrow(new DataException(""));
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = previousPeriodController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    private void mockSmallFull() {
        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
    }

    private void mockPreviousPeriodLinkOnSmallFullResource() {
        HashMap<String, String> links = new HashMap<>();
        links.put(SmallFullLinkType.PREVIOUS_PERIOD.getLink(), "link");
        when(smallFull.getLinks()).thenReturn(links);
    }
}