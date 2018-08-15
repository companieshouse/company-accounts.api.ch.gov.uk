package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountControllerTest {

    @Mock
    private HttpSession httpSessionMock;

    @Mock
    private Transaction transactionMock;

    @Mock
    private CompanyAccount companyAccountMock;

    @Mock
    private CompanyAccountService companyAccountServiceMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private CompanyAccountController companyAccountController;

    @BeforeEach
    public void setUp() {
        when(httpServletRequestMock.getSession()).thenReturn(httpSessionMock);
        when(httpSessionMock.getAttribute("transaction")).thenReturn(transactionMock);
        when(httpServletRequestMock.getHeader("X-Request-Id")).thenReturn("test");

    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource and patching transaction resource")
    void canCreateAccountSuccesfully() {
        ResponseObject responseObject = new ResponseObject(ResponseStatus.SUCCESS_CREATED,
            companyAccountMock);
        when(companyAccountServiceMock
            .createCompanyAccount(companyAccountMock, transactionMock, "test"))
            .thenReturn(responseObject);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());
        when(apiResponseMapper.map(
            responseObject.getStatus(), responseObject.getData(), responseObject.getErrorData()))
            .thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
            .createCompanyAccount(companyAccountMock, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(companyAccountMock, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to duplicate key error")
    void canCreateAccountWithDuplicateKeyError() {
        ResponseObject responseObject = new ResponseObject(ResponseStatus.DUPLICATE_KEY_ERROR,
            companyAccountMock);
        when(companyAccountServiceMock
            .createCompanyAccount(companyAccountMock, transactionMock, "test"))
            .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        when(apiResponseMapper.map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getErrorData())).thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
            .createCompanyAccount(companyAccountMock, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to an internal error (MongoException")
    void canCreateAccountWithInternalError() {
        ResponseObject responseObject = new ResponseObject(ResponseStatus.MONGO_ERROR,
            companyAccountMock);
        when(companyAccountServiceMock
            .createCompanyAccount(companyAccountMock, transactionMock, "test"))
            .thenReturn(responseObject);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(null);
        when(apiResponseMapper.map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getErrorData())).thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
            .createCompanyAccount(companyAccountMock, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}