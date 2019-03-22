package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.FixedAssetsInvestmentsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FixedAssetsInvestmentsControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String FIXED_ASSETS_INVESTMENTS_ID = "fixedAssetsInvestmentsId";

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private FixedAssetsInvestments mockFixedAssetsInvestments;

    @Mock
    private FixedAssetsInvestmentsService mockFixedAssetsInvestmentsService;

    @Mock
    private ApiResponseMapper mockApiResponseMapper;

    @Mock
    private ErrorMapper mockErrorMapper;

    @Mock
    private SmallFull mockSmallFull;

    @Mock
    private Map<String, String> mockSmallFullLinks;

    @InjectMocks
    private FixedAssetsInvestmentsController controller;

    @Test
    @DisplayName("Fixed assests investments resource created successfully")
    void createFixedAssetsInvestmentsResource() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
                mockFixedAssetsInvestments);
        when(mockFixedAssetsInvestmentsService.create(mockFixedAssetsInvestments, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mockFixedAssetsInvestments, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create fixed assets investments has failed - data exception thrown")
    void createFixedAssetsInvestmentsDataException() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockFixedAssetsInvestmentsService.create(mockFixedAssetsInvestments, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Create fixed assets investments - has binding errors")
    void createFixedAssetsInvestmentsBindingErrors() {

        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                controller.create(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Update fixed assets investments - no small full link")
    void updateFixedAssetsInvestmentsNoSmallFullLink() {

        when(mockRequest.getAttribute(AttributeName.SMALLFULL.getValue())).thenReturn(mockSmallFull);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
                controller.update(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update fixed assets investments - has binding errors")
    void updateFixedAssetsInvestmentsBindingErrors() {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                controller.update(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update fixed assets investments - success")
    void updateFixedAssetsInvestmentsSuccess() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockFixedAssetsInvestments);
        when(mockFixedAssetsInvestmentsService.update(mockFixedAssetsInvestments, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(mockApiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update fixed assets investments - data exception thrown")
    void updateFixedAssetsInvestmentsDataException() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        DataException dataException = new DataException("");
        when(mockFixedAssetsInvestmentsService.update(mockFixedAssetsInvestments, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockFixedAssetsInvestments, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get fixed assets investments - success")
    void getFixedAssetsInvestmentsSuccess() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockFixedAssetsInvestmentsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(FIXED_ASSETS_INVESTMENTS_ID);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND,
                mockFixedAssetsInvestments);
        when(mockFixedAssetsInvestmentsService.findById(FIXED_ASSETS_INVESTMENTS_ID, mockRequest))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(mockApiResponseMapper.mapGetResponse(responseObject.getData(), mockRequest))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(mockFixedAssetsInvestments, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get fixed assets investments - data exception thrown")
    void getFixedAssetsInvestmentsDataException() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);
        when(mockFixedAssetsInvestmentsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(FIXED_ASSETS_INVESTMENTS_ID);

        DataException dataException = new DataException("");
        when(mockFixedAssetsInvestmentsService.findById(FIXED_ASSETS_INVESTMENTS_ID, mockRequest))
                .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Delete fixed assets investments - success")
    void deleteFixedAssetsInvestmentsSuccess() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockFixedAssetsInvestments);

        when(mockFixedAssetsInvestmentsService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
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
    @DisplayName("Delete fixed assets investments - data exception thrown")
    void deleteFixedAssetsInvestmentsDataException() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");

        when(mockFixedAssetsInvestmentsService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
            .thenThrow(dataException);

        ResponseEntity responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(mockRequest.getAttribute(anyString())).thenReturn(mockSmallFull).thenReturn(mockTransaction);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE.getLink())).thenReturn("");
    }
}
