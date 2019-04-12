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
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportApproval;
import uk.gov.companieshouse.api.accounts.service.impl.CicReportApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CicReportApprovalControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CicReportApproval cicReportApproval;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private CicReportApprovalService cicReportApprovalService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private CicReportApprovalController cicReportApprovalController;

    @BeforeEach
    public void setUp() {
        when(request.getAttribute("transaction")).thenReturn(transaction);
    }

    @Test
    @DisplayName("Tests the successful creation of a CicReportApproval resource")
    public void canCreateCicReportApproval() throws DataException {
        ResponseObject successCreateResponse = new ResponseObject(ResponseStatus.CREATED,
            cicReportApproval);
        doReturn(successCreateResponse).when(cicReportApprovalService)
            .create(any(CicReportApproval.class), any(Transaction.class), anyString(),
                any(HttpServletRequest.class));
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(successCreateResponse.getData());
        when(apiResponseMapper.map(successCreateResponse.getStatus(),
            successCreateResponse.getData(), successCreateResponse.getErrors()))
            .thenReturn(responseEntity);

        ResponseEntity response = cicReportApprovalController
            .create(cicReportApproval, bindingResult, "", request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(cicReportApproval, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create CicReportApproval")
    void createCicReportApprovalError() throws DataException {

        when(cicReportApprovalService.create(any(), any(), any(), any()))
            .thenThrow(new DataException(""));
        when(apiResponseMapper.getErrorResponse())
            .thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity response = cicReportApprovalController
            .create(cicReportApproval, bindingResult, "", request);

        verify(cicReportApprovalService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the retreval of a CicReportApproval resource")
    public void canRetrieveCicReportApproval() throws DataException {
        ResponseObject successFindResponse = new ResponseObject(ResponseStatus.FOUND,
            cicReportApproval);
        doReturn(successFindResponse).when(cicReportApprovalService).find("123456", request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK)
            .body(cicReportApproval);
        when(apiResponseMapper.mapGetResponse(cicReportApproval,
            request)).thenReturn(responseEntity);

        ResponseEntity response = cicReportApprovalController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(cicReportApproval, response.getBody());
    }
}
