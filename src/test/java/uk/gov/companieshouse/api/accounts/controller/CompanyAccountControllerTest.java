package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountControllerTest {

    @Mock
    private Transaction transactionMock;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private CompanyAccountTransformer companyAccountTransformer;

    @Mock
    private CompanyAccount companyAccount;

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

    }

    @Test
    @DisplayName("Tests the successful creation of an company account resource and patching transaction resource")
    void canCreateAccountSuccesfully() throws DataException, PatchException {
        when(httpServletRequestMock.getAttribute("transaction")).thenReturn(transactionMock);
        when(httpServletRequestMock.getHeader("X-Request-Id")).thenReturn("test");
        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.CREATED,
            companyAccount);
        when(companyAccountServiceMock
                .create(companyAccount, transactionMock, "test"))
                .thenReturn(responseObject);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(
                responseObject.getStatus(), responseObject.getData(),
                responseObject.getValidationErrorData()))
                .thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
                .createCompanyAccount(companyAccount, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(companyAccount, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to duplicate key error")
    void canCreateAccountWithDuplicateKeyError() throws DataException, PatchException {
        when(httpServletRequestMock.getAttribute("transaction")).thenReturn(transactionMock);
        when(httpServletRequestMock.getHeader("X-Request-Id")).thenReturn("test");
        ResponseObject responseObject = new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR,
            companyAccount);
        when(companyAccountServiceMock
                .create(companyAccount, transactionMock, "test"))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        when(apiResponseMapper.map(responseObject.getStatus(),
                responseObject.getData(), responseObject.getValidationErrorData()))
                .thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
                .createCompanyAccount(companyAccount, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DisplayName("Tests the unsuccessful creation of an company account resource due to an internal error (MongoException")
    void canCreateAccountWithInternalError() throws DataException, PatchException {
        when(httpServletRequestMock.getAttribute("transaction")).thenReturn(transactionMock);
        when(httpServletRequestMock.getHeader("X-Request-Id")).thenReturn("test");
        doThrow(mock(DataException.class)).when(companyAccountServiceMock)
                .create(companyAccount, transactionMock, "test");
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        when(apiResponseMapper.map(any(DataException.class)))
                .thenReturn(responseEntity);

        ResponseEntity response = companyAccountController
                .createCompanyAccount(companyAccount, httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Tests the successful get of a company account resource")
    public void canGetCompanyAccount() throws NoSuchAlgorithmException {
        doReturn(companyAccount).when(httpServletRequestMock)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        ResponseEntity response = companyAccountController
                .getCompanyAccount(httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(companyAccount, response.getBody());
    }

    @Test
    @DisplayName("Tests the unsuccessful get of a company account resource")
    public void getCompanyAccountFail() throws NoSuchAlgorithmException {
        doReturn(null).when(httpServletRequestMock)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
        ResponseEntity response = companyAccountController
                .getCompanyAccount(httpServletRequestMock);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}