package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.TangibleAssetsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TangibleAssetsControllerTest {

    @Mock
    private TangibleAssetsService tangibleAssetsService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private TangibleAssets tangibleAssets;

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
    private TangibleAssetsController controller;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";
    private static final String GENERATED_ID = "generatedId";
    private static final String TANGIBLE_ASSETS_LINK = "tangibleAssetsLink";

    @Test
    @DisplayName("Tests the successful creation of a tangible assets resource")
    void createTangibleAssetsSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, tangibleAssets);
        when(tangibleAssetsService.create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(tangibleAssets, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, times(1))
                .create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the creation of a tangible assets resource when binding result errors are present")
    void createTangibleAssetsBindingResultErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.create(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, never())
                .create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
    }

    @Test
    @DisplayName("Tests the creation of a tangible assets resource where the service throws a data exception")
    void createTangibleAssetsServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(tangibleAssetsService)
                .create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, times(1))
                .create(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a tangible assets resource")
    void getTangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(tangibleAssetsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(GENERATED_ID);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, tangibleAssets);
        when(tangibleAssetsService.findById(GENERATED_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(tangibleAssets, response.getBody());

        verify(tangibleAssetsService, times(1))
                .findById(GENERATED_ID, request);
        verify(apiResponseMapper, times(1))
                .mapGetResponse(responseObject.getData(), request);
    }

    @Test
    @DisplayName("Tests the retrieval of a tangible assets resource where the service throws a data exception")
    void getTangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);
        when(tangibleAssetsService.generateID(COMPANY_ACCOUNTS_ID)).thenReturn(GENERATED_ID);

        doThrow(new DataException("")).when(tangibleAssetsService).findById(GENERATED_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(tangibleAssetsService, times(1))
                .findById(GENERATED_ID, request);
        verify(apiResponseMapper, never()).mapGetResponse(any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful update of a tangible assets resource")
    void updateTangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.TANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(TANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, tangibleAssets);
        when(tangibleAssetsService.update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, times(1))
                .update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the update of a tangible assets resource when the small full link doesn't exist")
    void updateTangibleAssetsNoSmallFullLink() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.TANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                controller.update(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
        verify(tangibleAssetsService, never())
                .update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
    }

    @Test
    @DisplayName("Tests the update of a tangible assets resource when binding result errors are present")
    void updateTangibleAssetsBindingResultErrors() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.TANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(TANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.update(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, never())
                .update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
    }

    @Test
    @DisplayName("Tests the update of a tangible assets resource where the service throws a data exception")
    void updateTangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.TANGIBLE_ASSETS_NOTE.getLink()))
                .thenReturn(TANGIBLE_ASSETS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(tangibleAssetsService)
                .update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(tangibleAssets, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(tangibleAssetsService, times(1))
                .update(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful deletion of a tangible assets resource")
    void deleteTangibleAssetsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(tangibleAssetsService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(tangibleAssetsService, times(1)).delete(COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the deletion of a tangible assets resource where the service throws a data exception")
    void deleteTangibleAssetsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(tangibleAssetsService).delete(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(tangibleAssetsService, times(1)).delete(COMPANY_ACCOUNTS_ID, request);
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }
}
