package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private Map<String, String> companyAccountLinks;

    @Mock
    private SmallFull smallFull;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private Errors errors;

    @InjectMocks
    private SmallFullController smallFullController;

    private static final String SMALL_FULL_LINK = "smallFullLink";
    
    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Test
    @DisplayName("Create small full - success")
    void createSmallFullSuccess() throws DataException {

        ResponseObject<SmallFull> responseObject = new ResponseObject<>(
            ResponseStatus.CREATED,
            smallFull);

        doReturn(transaction).when(request)
            .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
            .body(responseObject.getData());

        doReturn(responseObject).when(smallFullService)
            .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        doReturn(responseEntity).when(apiResponseMapper).map(responseObject.getStatus(),
            responseObject.getData(), responseObject.getErrors());
        ResponseEntity response = smallFullController.create(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(smallFull, response.getBody());
    }

    @Test
    @DisplayName("Create small full - binding result errors")
    void createSmallFullBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);

        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response = smallFullController.create(smallFull, bindingResult, COMPANY_ACCOUNTS_ID,  request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(request, never()).getAttribute(AttributeName.TRANSACTION.getValue());
    }

    @Test
    @DisplayName("Create small full - DataException")
    void createSmallFullDataException() throws DataException {

        doReturn(transaction).when(request)
                .getAttribute(AttributeName.TRANSACTION.getValue());

        doThrow(DataException.class).when(smallFullService)
                .create(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);

        when(apiResponseMapper.getErrorResponse()).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        ResponseEntity response = smallFullController.create(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Get small full - success")
    void getSmallFullSuccess() {

        doReturn(smallFull).when(request)
            .getAttribute(AttributeName.SMALLFULL.getValue());
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.OK).body(smallFull);
        when(apiResponseMapper.mapGetResponse(smallFull, request)).thenReturn(responseEntity);
        ResponseEntity response = smallFullController.get(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(smallFull, response.getBody());
    }

    @Test
    @DisplayName("Get small full - not found")
    void getSmallFullNotFound() {

        doReturn(null).when(request)
            .getAttribute(AttributeName.SMALLFULL.getValue());
        when(apiResponseMapper.mapGetResponse(null, request)).thenReturn(ResponseEntity.status(
            HttpServletResponse.SC_NOT_FOUND).build());
        ResponseEntity response = smallFullController.get(request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }

    @Test
    @DisplayName("Update small full - success")
    void updateSmallFullSuccess() throws DataException {

        ResponseObject<SmallFull> responseObject = new ResponseObject<>(
                ResponseStatus.UPDATED,
                smallFull);

        doReturn(companyAccount).when(request)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        when(companyAccount.getLinks()).thenReturn(companyAccountLinks);

        when(companyAccountLinks.get(CompanyAccountLinkType.SMALL_FULL.getLink())).thenReturn(SMALL_FULL_LINK);

        doReturn(transaction).when(request)
                .getAttribute(AttributeName.TRANSACTION.getValue());

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        doReturn(responseObject).when(smallFullService)
                .update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);
        doReturn(responseEntity).when(apiResponseMapper).map(responseObject.getStatus(),
                responseObject.getData(), responseObject.getErrors());
        ResponseEntity response = smallFullController.update(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Update small full - not found")
    void updateSmallFullNotFound() {

        doReturn(companyAccount).when(request)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        when(companyAccount.getLinks()).thenReturn(companyAccountLinks);

        when(companyAccountLinks.get(CompanyAccountLinkType.SMALL_FULL.getLink())).thenReturn(null);
        ResponseEntity response = smallFullController.update(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Update small full - binding result errors")
    void updateSmallFullBindingResultErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);

        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response = smallFullController.update(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(request, never()).getAttribute(anyString());
    }

    @Test
    @DisplayName("Update small full - DataException")
    void updateSmallFullDataException() throws DataException {

        doReturn(companyAccount).when(request)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        when(companyAccount.getLinks()).thenReturn(companyAccountLinks);

        when(companyAccountLinks.get(CompanyAccountLinkType.SMALL_FULL.getLink())).thenReturn(SMALL_FULL_LINK);

        doReturn(transaction).when(request)
                .getAttribute(AttributeName.TRANSACTION.getValue());

        doThrow(DataException.class).when(smallFullService)
                .update(smallFull, transaction, COMPANY_ACCOUNTS_ID, request);

        when(apiResponseMapper.getErrorResponse()).thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        ResponseEntity response = smallFullController.update(smallFull, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}