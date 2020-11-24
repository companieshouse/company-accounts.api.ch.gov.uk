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

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

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
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CicApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CicApprovalControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CicApproval cicApproval;

    @Mock
    private CicReport cicReport;

    @Mock
    private Map<String, String> cicReportLinks;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private CicApprovalService cicApprovalService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @InjectMocks
    private CicApprovalController cicApprovalController;





    @Test
    @DisplayName("Tests the successful creation of a CicApproval resource")
    void canCreateCicReportApproval() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        ResponseObject successCreateResponse = new ResponseObject(ResponseStatus.CREATED,
                cicApproval);
        doReturn(successCreateResponse).when(cicApprovalService)
            .create(any(CicApproval.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(successCreateResponse.getData());
        when(apiResponseMapper.map(successCreateResponse.getStatus(),
            successCreateResponse.getData(), successCreateResponse.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity response = cicApprovalController
            .create(cicApproval, bindingResult, "", request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cicApproval, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create CicApproval")
    void createCicReportApprovalError() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        when(cicApprovalService.create(any(), any(), any(), any()))
            .thenThrow(new DataException(""));
        when(apiResponseMapper.getErrorResponse())
            .thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity response = cicApprovalController
            .create(cicApproval, bindingResult, "", request);

        verify(cicApprovalService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the retreval of a CicApproval resource")
    void canRetrieveCicReportApproval() throws DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        ResponseObject successFindResponse = new ResponseObject(ResponseStatus.FOUND,
                cicApproval);
        doReturn(successFindResponse).when(cicApprovalService).find("123456", request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK)
            .body(cicApproval);
        when(apiResponseMapper.mapGetResponse(cicApproval,
            request)).thenReturn(responseEntity);

        ResponseEntity response = cicApprovalController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cicApproval, response.getBody());
    }
    @Test
    @DisplayName("Update CicApproval - no cic report link")
    void updateCicReportApprovalNoCicReportLink() {

        when(request.getAttribute(AttributeName.CIC_REPORT.getValue())).thenReturn(cicReport);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.APPROVAL.getLink())).thenReturn(null);

        ResponseEntity responseEntity =
            cicApprovalController.update(cicApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update CicApproval - has binding errors")
    void updateCicReportApprovalBindingErrors() {

        when(request.getAttribute(AttributeName.CIC_REPORT.getValue())).thenReturn(cicReport);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.APPROVAL.getLink())).thenReturn("");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
            cicApprovalController.update(cicApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update CicApproval - success")
    void updateCicReportApprovalSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(cicReport).thenReturn(transaction);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.APPROVAL.getLink())).thenReturn("");
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED,
                cicApproval);
        when(cicApprovalService.update(cicApproval, transaction, COMPANY_ACCOUNTS_ID, request))
            .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            cicApprovalController.update(cicApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update CicApproval - data exception thrown")
    void updateCicReportApprovalsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(cicReport).thenReturn(transaction);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.APPROVAL.getLink())).thenReturn("");
        when(bindingResult.hasErrors()).thenReturn(false);

        when(cicApprovalService.update(cicApproval, transaction, COMPANY_ACCOUNTS_ID, request))
            .thenThrow(new DataException(""));

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
            cicApprovalController.update(cicApproval, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

}

