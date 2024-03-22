package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class ApprovalControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Approval approval;
    
    @Mock
    private SmallFull smallFull;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ApprovalService approvalService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;
    
    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private ApprovalController approvalController;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String APPROVAL_LINK = "approvalLink";

    @Test
    @DisplayName("Tests the successful creation of a approval resource")
    void createApprovalSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.CREATED, approval);
        when(approvalService.create(approval, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                approvalController.create(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(approval, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the creation of a approval resource when binding result errors are present")
    void createApprovalBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                approvalController.create(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the creation of a approval resource where the service throws a data exception")
    void createApprovalServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(approvalService)
                .create(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                approvalController.create(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful retrieval of a approval resource")
    void getApprovalSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Approval> responseObject = new ResponseObject<>(ResponseStatus.FOUND, approval);
        when(approvalService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = approvalController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(approval, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of a approval resource where the service throws a data exception")
    void getApprovalServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(approvalService).find(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = approvalController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a approval resource")
    void updateApprovalSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.APPROVAL.getLink()))
                .thenReturn(APPROVAL_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject<Approval> responseObject = new ResponseObject<>(ResponseStatus.UPDATED, approval);
        when(approvalService.update(approval, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                approvalController.update(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the update of a approval resource when the small full link doesn't exist")
    void updateApprovalNoSmallFullLink() {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.APPROVAL.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                approvalController.update(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of a approval resource when binding result errors are present")
    void updateApprovalBindingResultErrors() {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.APPROVAL.getLink()))
                .thenReturn(APPROVAL_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                approvalController.update(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the update of a approval resource where the service throws a data exception")
    void updateApprovalServiceThrowsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.APPROVAL.getLink()))
                .thenReturn(APPROVAL_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(approvalService)
                .update(approval, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                approvalController.update(approval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful deletion of a approval resource")
    void deleteApprovalSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(approvalService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = approvalController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the deletion of a approval resource where the service throws a data exception")
    void deleteApprovalServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(approvalService).delete(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = approvalController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}