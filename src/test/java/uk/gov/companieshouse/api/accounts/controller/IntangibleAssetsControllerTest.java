package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.IntangibleAssetsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntangibleAssetsControllerTest {

    @Mock
    private IntangibleAssetsService intangibleAssetsService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private IntangibleAssets intangibleAssets;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Errors errors;

    @Mock
    private SmallFull smallFull;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private IntangibleAssetsController controller;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String INTANGIBLE_ASSETS_LINK = "intangibleAssetsLink";

    @Test
    @DisplayName("Tests the successful creation of intangible assets resource")
    void createIntangibleAssetsSuccess() throws DataException {
        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, intangibleAssets);
        when(intangibleAssetsService.create(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(intangibleAssets, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the creation of a intangible assets resource when binding result errors are present")
    void createIntangibleAssetsBindingResultErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.create(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the creation of a intangible assets resource where the service throws a data exception")
    void createIntangibleAssetsServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(intangibleAssetsService)
                .create(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the successful retrieval of a intangible assets resource")
    void getIntangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, intangibleAssets);
        when(intangibleAssetsService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(intangibleAssets, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of a intangible assets resource where the service throws a data exception")
    void getIntangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(intangibleAssetsService).find(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a intangible assets resource")
    void updateIntangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.INTANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(INTANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, intangibleAssets);
        when(intangibleAssetsService.update(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the update of a intangible assets resource when the small full link doesnt exist")
    void updateIntangibleAssetsNoSmallFullLink() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.INTANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                controller.update(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of an intangible assets resource when binding result errors are present")
    void updateIntangibleAssetsBindingResultErrors() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.INTANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(INTANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.update(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the update of an intangible assets resource where the service throws a data exception")
    void updateIntangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.INTANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(INTANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(intangibleAssetsService)
                .update(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(intangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the successful deletion of an intangible assets resource")
    void deleteIntangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(intangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the deletion of an intangible assets resource where the service throws a data exception")
    void deleteIntangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(intangibleAssetsService).delete(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
