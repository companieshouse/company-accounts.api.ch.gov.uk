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

import jakarta.servlet.http.HttpServletRequest;

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
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class DirectorsControllerTest {

    @Mock
    private DirectorService directorService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private Director directorRest;

    @Mock
    private DirectorsReport directorsReport;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Errors errors;

    @Mock
    private Map<String, String> directors;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private DirectorsController controller;

    private static final String DIRECTORS_ID = "directorsId";
    private static final String COMPANY_ACCOUNT_ID = "companyAccountId";
    private static final String DIRECTOR_LINK = "directorsLink";

    @Test
    @DisplayName("Tests the successful creation of a director")
    void createDirectorSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Director> responseObject = new ResponseObject<>(ResponseStatus.CREATED, directorRest);
        when(directorService.create(directorRest, transaction, DIRECTORS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(directorRest, bindingResult, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(directorRest, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, times(1))
                .create(directorRest, transaction, DIRECTORS_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());

    }

    @Test
    @DisplayName("Tests the creation of a Director when there are binding result errors")
    void createDirectorWithBindingResultErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.create(directorRest, bindingResult, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, never())
                .create(directorRest, transaction, DIRECTORS_ID, request);

    }

    @Test
    @DisplayName("Tests the creation of a Director where the service throws a data exception")
    void createDirectorAndServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(directorService)
                .create(directorRest, transaction, DIRECTORS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.create(directorRest, bindingResult, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, times(1))
                .create(directorRest, transaction, DIRECTORS_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of a Director")
    void getDirectorSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject<Director> responseObject = new ResponseObject<>(ResponseStatus.FOUND, directorRest);
        when(directorService.find(DIRECTORS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.get(DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(directorRest, response.getBody());

        verify(directorService, times(1))
                .find(DIRECTORS_ID, request);
        verify(apiResponseMapper, times(1))
                .mapGetResponse(responseObject.getData(), request);
    }

    @Test
    @DisplayName("Tests the retrieval of a director when the service throws a DataException")
    void getDirectorServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(directorService).find(DIRECTORS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.get(DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(directorService, times(1))
                .find(DIRECTORS_ID, request);
        verify(apiResponseMapper, never()).mapGetResponse(any(), any());
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful retrieval of all Directors")
    void getAllDirectorsSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        Director[] directors = new Director[0];
        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, directors);
        when(directorService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getDataForMultipleResources());
        when(apiResponseMapper.mapGetResponseForMultipleResources(responseObject.getDataForMultipleResources(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(directors, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of all Directors when the service throws a DataException")
    void getAllDirectorsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        when(directorService.findAll(transaction, COMPANY_ACCOUNT_ID, request))
                .thenThrow(DataException.class);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.getAll(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a director resource")
    void updateDirectorSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);

        when(directorsReport.getDirectors()).thenReturn(directors);
        when(directors.get(DIRECTORS_ID)).thenReturn(DIRECTOR_LINK);


        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject<Director> responseObject = new ResponseObject<>(ResponseStatus.UPDATED, directorRest);
        when(directorService.update(directorRest, transaction, COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(directorRest, bindingResult, COMPANY_ACCOUNT_ID, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, times(1))
                .update(directorRest, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the update of a director when the Director ID doesnt exist")
    void UpdateDirectorResourceWhenIdIsNull() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);

        when(directorsReport.getDirectors()).thenReturn(directors);
        when(directors.get(DIRECTORS_ID)).thenReturn(null);

        ResponseEntity response =
                controller.update(directorRest, bindingResult, COMPANY_ACCOUNT_ID, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of a director resource when binding result errors are present")
    void updateDirectorBindingResultErrors() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);

        when(directorsReport.getDirectors()).thenReturn(directors);
        when(directors.get(DIRECTORS_ID)).thenReturn(DIRECTOR_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                controller.update(directorRest, bindingResult, COMPANY_ACCOUNT_ID, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());

        verify(errorMapper, times(1)).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, never())
                .update(directorRest, transaction, COMPANY_ACCOUNT_ID, request);
    }

    @Test
    @DisplayName("Tests the update of a directors resource where the service throws a data exception")
    void updateDirectorsServiceThrowsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(directorsReport).thenReturn(transaction);

        when(directorsReport.getDirectors()).thenReturn(directors);
        when(directors.get(DIRECTORS_ID)).thenReturn(DIRECTOR_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(directorService)
                .update(directorRest, transaction, COMPANY_ACCOUNT_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                controller.update(directorRest, bindingResult, COMPANY_ACCOUNT_ID, DIRECTORS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
        verify(directorService, times(1))
                .update(directorRest, transaction, COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .getErrorResponse();
    }

    @Test
    @DisplayName("Tests the successful deletion of a director resource")
    void deleteDirectorSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(directorService.delete(COMPANY_ACCOUNT_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(directorService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1))
                .map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());
    }

    @Test
    @DisplayName("Tests the deletion of a Director resource where the service throws a data exception")
    void deleteDirectorServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(directorService).delete(COMPANY_ACCOUNT_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = controller.delete(COMPANY_ACCOUNT_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(directorService, times(1)).delete(COMPANY_ACCOUNT_ID, request);
        verify(apiResponseMapper, times(1)).getErrorResponse();
    }
}
