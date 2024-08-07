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
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalControllerTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private DirectorsApproval directorsApproval;

    @Mock
    private Errors errors;

    @Mock
    private DirectorsApprovalService directorsApprovalService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private Map<String, String> directorsReportLink;

    @InjectMocks
    private DirectorsApprovalController directorsApprovalController;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";

    private static final String DIRECTORS_APPROVAL_LINK = "directorsApprovalLink";

    @Test
    @DisplayName("Tests the successful creation of a directors approval resource")
    void canCreateDirectorsApproval() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        ResponseObject<DirectorsApproval> successCreateResponse = new ResponseObject<>(ResponseStatus.CREATED,
                directorsApproval);
        doReturn(successCreateResponse).when(directorsApprovalService)
                .create(any(DirectorsApproval.class), any(Transaction.class), anyString(),
                        any(HttpServletRequest.class));
        ResponseEntity<DirectorsApproval> responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(successCreateResponse.getData());
        when(apiResponseMapper.map(successCreateResponse.getStatus(),
                successCreateResponse.getData(), successCreateResponse.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> response = directorsApprovalController
                .create(directorsApproval, bindingResult,  COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(directorsApproval, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create directors approval")
    void createDirectorsApprovalError() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        when(directorsApprovalService.create(any(), any(), any(), any())).thenThrow(new DataException(""));
        when(apiResponseMapper.getErrorResponse())
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<?> response = directorsApprovalController
                .create(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        verify(directorsApprovalService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the retrieval of a directors approval resource")
    void canRetrieveDirectorsApproval() throws DataException {
        ResponseObject<?> successFindResponse = new ResponseObject<>(ResponseStatus.FOUND,
                directorsApproval);
        doReturn(successFindResponse).when(directorsApprovalService).find("123456", request);

        ResponseEntity<DirectorsApproval> responseEntity = ResponseEntity.status(HttpStatus.OK).body(directorsApproval);
        when(apiResponseMapper.mapGetResponse(directorsApproval, request)).thenReturn(responseEntity);

        ResponseEntity<?> response = directorsApprovalController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(directorsApproval, response.getBody());
    }

    @Test
    @DisplayName("Tests the creation of a directors approval resource when binding result errors are present")
    void createDirectorsApprovalBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity<?> response =
                directorsApprovalController.create(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("create directors approval resource throws mongo exception")
    void createDirectorsApprovalThrowsMongoException() throws DataException {
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(directorsApprovalService.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(new DataException(""));

        when(apiResponseMapper.getErrorResponse())
                .thenReturn(createResponseEntity());

        ResponseEntity<?> response =
                directorsApprovalController.get(COMPANY_ACCOUNTS_ID, request);

        assertDirectorsApprovalControllerResponse(response);
        verify(directorsApprovalService, times(1)).find(COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Update directors approval resource - no directors approval link")
    void updateDirectorsApprovalResourceNoDirectorsApprovalLink() {
        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);
        when(directorsReport.getLinks()).thenReturn(directorsReportLink);
        when(directorsReportLink.get(DirectorsReportLinkType.APPROVAL.getLink())).thenReturn(null);

        ResponseEntity<?> responseEntity =
                directorsApprovalController.update(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
    @Test
    @DisplayName("Update directors approval resource - success")
    void updateDirectorsApprovalResourceSuccess() throws DataException {
        mockTransactionAndLinks();

        ResponseObject<DirectorsApproval> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                directorsApproval);
        when(directorsApprovalService.update(directorsApproval, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors())).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                directorsApprovalController.update(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update directors approval resource - data exception thrown")
    void updateDirectorsApprovalResourceDataException() throws DataException {
       mockTransactionAndLinks();

        when(directorsApprovalService.update(directorsApproval, transaction,
                COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse =
                directorsApprovalController.update(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests the update of an directors approval resource when binding result errors are present")
    void updateDirectorsApprovalBindingResultErrors() {
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity<?> response =
                directorsApprovalController.update(directorsApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Delete directors approval resource - success")
    void deleteDirectorsApprovalResourceSuccess() throws DataException {
        when(request.getAttribute(anyString())).thenReturn(transaction);

        ResponseObject<DirectorsApproval> responseObject = new ResponseObject<>(ResponseStatus.UPDATED,
                directorsApproval);

        when(directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity<Object> responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = directorsApprovalController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Delete directors approval resource - data exception thrown")
    void deleteDirectorsApprovalResourceDataException() throws DataException {
        when(request.getAttribute(anyString())).thenReturn(transaction);

        DataException dataException = new DataException("");

        when(directorsApprovalService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(dataException);

        ResponseEntity<Object> responseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity<?> returnedResponse = directorsApprovalController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    private ResponseEntity<?> createResponseEntity() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private void assertDirectorsApprovalControllerResponse(ResponseEntity<?> response) {
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    private void mockTransactionAndLinks() {
        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);
        when(directorsReport.getLinks()).thenReturn(directorsReportLink);
        when(directorsReportLink.get(DirectorsReportLinkType.APPROVAL.getLink())).thenReturn(DIRECTORS_APPROVAL_LINK);
    }
}