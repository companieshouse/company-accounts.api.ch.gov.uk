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
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.EmployeesService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmployeesControllerTest {

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Mock
    private BindingResult mockBindingResult;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private Transaction mockTransaction;

    @Mock
    private Employees mockEmployees;

    @Mock
    private EmployeesService mockEmployeesService;

    @Mock
    private ApiResponseMapper mockApiResponseMapper;

    @Mock
    private ErrorMapper mockErrorMapper;

    @Mock
    private SmallFull mockSmallFull;

    @Mock
    private Map<String, String> mockSmallFullLinks;

    @InjectMocks
    private EmployeesController controller;

    @Test
    @DisplayName("Employees resource created successfully")
    void createEmployeesResource() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject mockResponseObject = new ResponseObject(ResponseStatus.CREATED,
                mockEmployees);
        when(mockEmployeesService.create(mockEmployees, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(mockResponseObject);

        ResponseEntity mockResponseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(mockResponseObject.getData());
        when(mockApiResponseMapper.map(mockResponseObject.getStatus(), mockResponseObject.getData(),
                mockResponseObject.getErrors()))
                .thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockEmployees, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, returnedResponse.getStatusCode());
        assertEquals(mockEmployees, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Create employees has failed - data exception thrown")
    void createEmployeesDataException() throws DataException {

        when(mockBindingResult.hasErrors()).thenReturn(false);
        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockEmployeesService.create(mockEmployees, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse())
                .thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.create(mockEmployees, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, mockResponseEntity.getStatusCode());
        assertNull(mockResponseEntity.getBody());
    }

    @Test
    @DisplayName("Create employees - has binding errors")
    void createEmployeesBindingErrors() {

        when(mockBindingResult.hasErrors()).thenReturn(true);
        when(mockErrorMapper.mapBindingResultErrorsToErrorModel(mockBindingResult)).thenReturn(new Errors());

        ResponseEntity returnedResponse =
                controller.create(mockEmployees, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.BAD_REQUEST, returnedResponse.getStatusCode());
    }

    @Test
    @DisplayName("Delete employees - success")
    void deleteEmployeesSuccess() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        ResponseObject mockResponseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockEmployees);

        when(mockEmployeesService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenReturn(mockResponseObject);

        ResponseEntity mockResponseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();

        when(mockApiResponseMapper.map(mockResponseObject.getStatus(), mockResponseObject.getData(),
                mockResponseObject.getErrors()))
                .thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
    }

    @Test
    @DisplayName("Delete employees - data exception thrown")
    void deleteEmployeesDataException() throws DataException {

        when(mockRequest.getAttribute(anyString())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");

        when(mockEmployeesService.delete(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenThrow(dataException);

        ResponseEntity mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse = controller.delete(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
    }


    @Test
    @DisplayName("Update employees - success")
    void updateEmployeesSuccess() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        ResponseObject mockResponseObject = new ResponseObject(ResponseStatus.UPDATED,
                mockEmployees);
        when(mockEmployeesService.update(mockEmployees, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenReturn(mockResponseObject);

        ResponseEntity mockResponseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(mockApiResponseMapper.map(mockResponseObject.getStatus(), mockResponseObject.getData(),
                mockResponseObject.getErrors()))
                .thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockEmployees, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }


    @Test
    @DisplayName("Update employees - data exception thrown")
    void updateEmployeesDataException() throws DataException {

        mockTransactionAndLinks();
        when(mockBindingResult.hasErrors()).thenReturn(false);

        DataException dataException = new DataException("");
        when(mockEmployeesService.update(mockEmployees, mockTransaction,
                COMPANY_ACCOUNTS_ID, mockRequest)).thenThrow(dataException);

        ResponseEntity mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.update(mockEmployees, mockBindingResult,
                        COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get employees - success")
    void getEmployeesSuccess() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        ResponseObject mockResponseObject = new ResponseObject(ResponseStatus.FOUND,
                mockEmployees);
        when(mockEmployeesService.find(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenReturn(mockResponseObject);

        ResponseEntity mockResponseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(mockResponseObject.getData());
        when(mockApiResponseMapper.mapGetResponse(mockResponseObject.getData(), mockRequest))
                .thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse =
                controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, returnedResponse.getStatusCode());
        assertEquals(mockEmployees, returnedResponse.getBody());
    }

    @Test
    @DisplayName("Get employees - data exception thrown")
    void getEmployeesDataException() throws DataException {

        when(mockRequest.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(mockTransaction);

        DataException dataException = new DataException("");
        when(mockEmployeesService.find(COMPANY_ACCOUNTS_ID, mockRequest))
                .thenThrow(dataException);

        ResponseEntity mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(mockApiResponseMapper.getErrorResponse()).thenReturn(mockResponseEntity);

        ResponseEntity returnedResponse = controller.get(COMPANY_ACCOUNTS_ID, mockRequest);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, returnedResponse.getStatusCode());
        assertNull(returnedResponse.getBody());
    }

    private void mockTransactionAndLinks() {
        when(mockRequest.getAttribute(anyString())).thenReturn(mockSmallFull).thenReturn(mockTransaction);
        when(mockSmallFull.getLinks()).thenReturn(mockSmallFullLinks);
        when(mockSmallFullLinks.get(SmallFullLinkType.EMPLOYEES_NOTE.getLink())).thenReturn("");
    }
}
