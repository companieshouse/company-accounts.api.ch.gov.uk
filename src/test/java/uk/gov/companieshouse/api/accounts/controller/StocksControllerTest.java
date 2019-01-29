package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.Assertions;
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
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.StocksService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StocksControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String STOCKS_ID = "stocksId";

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private Stocks mockStocks;

    @Mock
    private StocksService mockStocksService;

    @Mock
    private ApiResponseMapper mockApiResponseMapper;

    @Mock
    private ErrorMapper mockErrorMapper;

    @Mock
    private SmallFull mockSmallFull;

    @Mock
    private Map<String, String> mockSmallFullLinks;

    @InjectMocks
    private StocksController controller;

    @Test
    @DisplayName("Stocks resource created successfully")
    void createStocksResource() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, mockStocks);
        when(mockStocksService.create(mockStocks, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockStocks, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create stocks has failed - data exception thrown")
    void createStocksDataException() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockStocksService.create(mockStocks, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.map(dataException))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create stocks - has binding errors")
    void createStocksBindingErrors() {

        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                controller.create(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update stocks - no small full link")
    void updateStocksNoSmallFullLink() {

        when(mockRequest.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(mockSmallFull);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.STOCKS_NOTE.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
                controller.update(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update stocks - has binding errors")
    void updateStocksBindingErrors() {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                controller.update(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update stocks - success")
    void updateStocksSuccess() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockStocks);
        when(mockStocksService.update(mockStocks, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update stocks - data exception thrown")
    void updateStocksDataException() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        DataException dataException = new DataException("");
        when(mockStocksService.update(mockStocks, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.map(dataException)).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockStocks, mockBindingResult, COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get stocks - success")
    void getStocksSuccess() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockStocksService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(STOCKS_ID);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND,
                mockStocks);
        when(mockStocksService.findById(STOCKS_ID, mockRequest))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(mockApiResponseMapper.mapGetResponse(responseObject.getData(), mockRequest))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(mockStocks, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get stocks - data exception thrown")
    void getStocksDataException() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockStocksService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(STOCKS_ID);

        DataException dataException = new DataException("");
        when(mockStocksService.findById(STOCKS_ID, mockRequest))
                .thenThrow(dataException);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.map(dataException)).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete stocks - success")

    void deleteStocksSuccess() throws DataException {
        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockStocks);

        when(mockStocksService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        Assertions.assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete stocks - data exception thrown")
    void deleteStocksDataException() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockStocksService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenThrow(dataException);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.map(dataException)).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        Assertions.assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(mockRequest.getAttribute(anyString())).thenReturn(mockSmallFull).thenReturn(mockTransaction);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.STOCKS_NOTE.getLink())).thenReturn("");
    }
}
