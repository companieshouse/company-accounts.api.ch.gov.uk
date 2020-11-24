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
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CicStatementsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CicStatementsControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CicReport cicReport;

    @Mock
    private CicStatements cicStatements;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private CicStatementsService service;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> cicReportLinks;

    @InjectMocks
    private CicStatementsController controller;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String CIC_STATEMENTS_LINK = "CicStatementsLink";

    @Test
    @DisplayName("Create CIC statements - success path")
    void createCicStatementsSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<CicStatements> responseObject = new ResponseObject<>(ResponseStatus.CREATED, cicStatements);
        when(service.create(cicStatements, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, returnedResponse.getStatusCode());
        assertEquals(cicStatements, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create CIC statements - binding result errors")
    void createCicStatementsBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                controller.create(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create CIC statements- data exception")
    void createCicStatementsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.create(cicStatements, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.create(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get CIC statements - success path")
    void getCicStatementsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<CicStatements> responseObject = new ResponseObject<>(ResponseStatus.FOUND, cicStatements);
        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, returnedResponse.getStatusCode());
        assertEquals(cicStatements, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get CIC statements - data exception")
    void getCicStatementsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.find(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC statements - success path")
    void updateCicStatementsSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(cicReport).thenReturn(transaction);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.STATEMENTS.getLink()))
                .thenReturn(CIC_STATEMENTS_LINK);
        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject<CicStatements> responseObject = new ResponseObject<>(ResponseStatus.UPDATED, cicStatements);
        when(service.update(cicStatements, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC statements - not found")
    void updateCicStatementsNotFound() {

        when(request.getAttribute(AttributeName.CIC_REPORT.getValue())).thenReturn(cicReport);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.STATEMENTS.getLink()))
                .thenReturn(null);

        ResponseEntity returnedResponse =
                controller.update(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NOT_FOUND, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC statements - binding result errors")
    void updateCicStatementsBindingResultErrors() {

        when(request.getAttribute(AttributeName.CIC_REPORT.getValue())).thenReturn(cicReport);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.STATEMENTS.getLink()))
                .thenReturn(CIC_STATEMENTS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity returnedResponse =
                controller.update(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
        assertEquals(errors, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Update CIC statements - data exception")
    void updateCicStatementsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(cicReport).thenReturn(transaction);
        when(cicReport.getLinks()).thenReturn(cicReportLinks);
        when(cicReportLinks.get(CicReportLinkType.STATEMENTS.getLink()))
                .thenReturn(CIC_STATEMENTS_LINK);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(service.update(cicStatements, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                controller.update(cicStatements, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete CIC statements - success path")
    void deleteCicStatementsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Delete CIC statements - data exception")
    void deleteCicStatementsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(service.delete(COMPANY_ACCOUNTS_ID, request)).thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }
}
