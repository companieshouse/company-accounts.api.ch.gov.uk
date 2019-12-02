package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.model.filing.Data;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorsReportServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DirectorsControllerTest {

    @Mock
    private DirectorService directorService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private Director directorRest;

    @Mock
    private DirectorsReportServiceImpl directorsReportService;

    @Mock
    private SmallFull smallFull;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private Errors errors;

    @Mock
    private ErrorMapper errorMapper;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private DirectorsController controller;

    private static final String DIRECTORS_ID = "directorsId";

    @Test
    @DisplayName("Tests the successful creation of a director")
    void createDirectorSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, directorRest);
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

}
