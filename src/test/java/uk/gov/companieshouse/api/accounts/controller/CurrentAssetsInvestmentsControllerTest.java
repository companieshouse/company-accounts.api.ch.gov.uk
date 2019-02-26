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
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentAssetsInvestmentsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentAssetsInvestmentsControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String CURRENT_ASSETS_INVESTMENTS_ID = "currentAssetsInvestmentsId";

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private CurrentAssetsInvestments mockCurrentAssetsInvestments;

    @Mock
    private CurrentAssetsInvestmentsService mockCurrentAssetsInvestmentsService;

    @Mock
    private ApiResponseMapper mockApiResponseMapper;

    @Mock
    private ErrorMapper mockErrorMapper;

    @Mock
    private SmallFull mockSmallFull;

    @Mock
    private Map<String, String> mockSmallFullLinks;

    @InjectMocks
    private CurrentAssetsInvestmentsController controller;

    @Test
    @DisplayName("Current assets investments resource created successfully")
    void createCurrentAssetsInvestmentsResource() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
            mockCurrentAssetsInvestments);
        when(mockCurrentAssetsInvestmentsService.create(mockCurrentAssetsInvestments, mockTransaction,
            COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
            responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.create(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockCurrentAssetsInvestments, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create current assets investments has failed - data exception thrown")
    void createCurrentAssetsInvestmentsDataException() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockCurrentAssetsInvestmentsService.create(mockCurrentAssetsInvestments, mockTransaction,
            COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.create(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create current assets investments - has binding errors")
    void createCurrentAssetsInvestmentsBindingErrors() {

        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
            controller.create(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update current assets investments - no small full link")
    void updateCurrentAssetsInvestmentsNoSmallFullLink() {

        when(mockRequest.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(mockSmallFull);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
            controller.update(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update current assets investments - has binding errors")
    void updateCurrentAssetsInvestmentsBindingErrors() {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
            controller.update(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update current assets investments - success")
    void updateCurrentAssetsInvestmentsSuccess() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
            mockCurrentAssetsInvestments);
        when(mockCurrentAssetsInvestmentsService.update(mockCurrentAssetsInvestments, mockTransaction,
            COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
            responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.update(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update current assets investments - data exception thrown")
    void updateCurrentAssetsInvestmentsDataException() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        DataException dataException = new DataException("");
        when(mockCurrentAssetsInvestmentsService.update(mockCurrentAssetsInvestments, mockTransaction,
            COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.update(mockCurrentAssetsInvestments, mockBindingResult,
                COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get current assets investments - success")
    void getCurrentAssetsInvestmentsSuccess() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockCurrentAssetsInvestmentsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(CURRENT_ASSETS_INVESTMENTS_ID);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND,
            mockCurrentAssetsInvestments);
        when(mockCurrentAssetsInvestmentsService.findById(CURRENT_ASSETS_INVESTMENTS_ID, mockRequest))
            .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
            .body(responseObject.getData());
        when(mockApiResponseMapper.mapGetResponse(responseObject.getData(), mockRequest))
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(mockCurrentAssetsInvestments, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get current assets investments - data exception thrown")
    void getCurrentAssetsInvestmentsDataException() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockCurrentAssetsInvestmentsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(CURRENT_ASSETS_INVESTMENTS_ID);

        DataException dataException = new DataException("");
        when(mockCurrentAssetsInvestmentsService.findById(CURRENT_ASSETS_INVESTMENTS_ID, mockRequest))
            .thenThrow(dataException);

        ResponseEntity responseEntity =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete current assets investments - success")
    void deleteCurrentAssetsInvestmentsSuccess() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
            mockCurrentAssetsInvestments);

        when(mockCurrentAssetsInvestmentsService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
            .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
            responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete current assets investments - data exception thrown")
    void deleteCurrentAssetsInvestmentsDataException() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");

        when(mockCurrentAssetsInvestmentsService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
            .thenThrow(dataException);

        ResponseEntity responseEntity =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(mockRequest.getAttribute(anyString())).thenReturn(mockSmallFull).thenReturn(mockTransaction);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE.getLink())).thenReturn("");
    }


}
