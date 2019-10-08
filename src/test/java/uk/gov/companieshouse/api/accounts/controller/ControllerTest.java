package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.resource.ParentResource;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ControllerTest {

    @Mock
    private RestObject restObject;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private ResourceService<RestObject> resourceService;

    @Mock
    private ParentResource parentResource;

    @Mock
    private LinkType linkType;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @InjectMocks
    private BaseController<RestObject> baseController
            = new BaseController<>(resourceService, parentResource, linkType, errorMapper, apiResponseMapper);

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Test
    @DisplayName("Create - has binding errors")
    void createHasBindingErrors() {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                baseController.create(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    @DisplayName("Create - success")
    void createSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, restObject);
        when(resourceService.create(restObject, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                baseController.create(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(restObject, responseEntity.getBody());
    }

    @Test
    @DisplayName("Create - data exception thrown")
    void createDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(resourceService.create(restObject, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse())
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                baseController.create(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Get - success")
    void getSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, restObject);
        when(resourceService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND)
                .body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                baseController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(restObject, responseEntity.getBody());
    }

    @Test
    @DisplayName("Get - data exception thrown")
    void getDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(resourceService.find(COMPANY_ACCOUNTS_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse = baseController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update - no parent resource link")
    void updateNoParentResourceLink() {

        when(parentResource.hasLink(request, linkType)).thenReturn(false);

        ResponseEntity responseEntity =
                baseController.update(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update - has binding errors")
    void updateHasBindingErrors() {

        when(parentResource.hasLink(request, linkType)).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(new Errors());

        ResponseEntity responseEntity =
                baseController.update(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update - success")
    void updateSuccess() throws DataException {

        when(parentResource.hasLink(request, linkType)).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, restObject);
        when(resourceService.update(restObject, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                baseController.update(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Update - data exception thrown")
    void updateDataException() throws DataException {

        when(parentResource.hasLink(request, linkType)).thenReturn(true);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(resourceService.update(restObject, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenThrow(new DataException(""));

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity returnedResponse =
                baseController.update(restObject, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(returnedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

}