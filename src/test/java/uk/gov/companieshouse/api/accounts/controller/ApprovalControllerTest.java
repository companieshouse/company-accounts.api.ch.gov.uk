package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
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
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.service.impl.ApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ApprovalControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Approval approval;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ApprovalService approvalService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private ApprovalController approvalController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException, DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        when(request.getHeader("X-Request-Id")).thenReturn("test");
    }

    @Test
    @DisplayName("Tests the successful creation of a approval resource")
    public void canCreateApproval() throws NoSuchAlgorithmException, DataException {
        ResponseObject successCreateResponse = new ResponseObject(ResponseStatus.CREATED,
            approval);
        doReturn(successCreateResponse).when(approvalService)
            .create(any(Approval.class), any(Transaction.class), anyString(), any(HttpServletRequest.class));
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(successCreateResponse.getData());
        when(apiResponseMapper.map(successCreateResponse.getStatus(),
            successCreateResponse.getData(), successCreateResponse.getValidationErrorData()))
            .thenReturn(responseEntity);

        ResponseEntity response = approvalController
            .create(approval, bindingResult, "", request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(approval, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful request to create Approval")
    void createApprovalError() throws DataException {
        DataException exception = new DataException("string");
        when(approvalService.create(any(), any(), any(), any())).thenThrow(exception);
        when(apiResponseMapper.map(exception))
            .thenReturn(new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity response = approvalController
            .create(approval, bindingResult, "", request);

        verify(approvalService, times(1)).create(any(), any(), any(), any());
        verify(apiResponseMapper, times(1)).map(exception);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Test the retreval of a approval resource")
    public void canRetrieveApproval() throws NoSuchAlgorithmException, DataException {
        ResponseObject successFindResponse = new ResponseObject(ResponseStatus.FOUND,
            approval);
        doReturn(successFindResponse).when(approvalService).findById("find", request);
        doReturn("find").when(approvalService).generateID("123456");
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(approval);
        when(apiResponseMapper.mapGetResponse(approval,
            request)).thenReturn(responseEntity);

        ResponseEntity response = approvalController.get("123456", request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(approval, response.getBody());
    }
}