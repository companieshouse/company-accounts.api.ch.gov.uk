package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.ApprovalService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@TestInstance(Lifecycle.PER_CLASS)
public class ApprovalControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private SmallFull smallFull;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private Approval approval;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ApprovalService approvalService;

    @Mock
    private Map<String, String> links;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private ApprovalController approvalController;

    @BeforeEach
    public void setUp() throws NoSuchAlgorithmException, DataException {
        when(request.getAttribute("transaction")).thenReturn(transaction);
        when(request.getHeader("X-Request-Id")).thenReturn("test");
        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED,
            approval);
        doReturn(responseObject).when(approvalService)
            .create(any(Approval.class), any(Transaction.class), anyString(), anyString());
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getValidationErrorData()))
            .thenReturn(responseEntity);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("companyAccountId", "123456");
        doReturn(pathVariables).when(request)
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        doReturn(transaction).when(request)
            .getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(smallFull).when(request).getAttribute(AttributeName.SMALLFULL.getValue());
        doReturn(responseObject).when(approvalService).findById("create", "test");
        doReturn(new ResponseObject(ResponseStatus.FOUND,
            approval)).when(approvalService).findById("find", "test");
        doReturn("123456").when(transaction).getCompanyNumber();
        doReturn(links).when(smallFull).getLinks();
        doReturn("7890").when(links).get("self");
    }

    @Test
    @DisplayName("Tests the successful creation of a approval resource")
    public void canCreateApproval() throws NoSuchAlgorithmException {
        ResponseEntity response = approvalController
            .create(approval, bindingResult, "123456", request);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(approval, response.getBody());
    }

    @Test
    @DisplayName("Test the retreval of a approval resource")
    public void canRetrieveApproval() throws NoSuchAlgorithmException {

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