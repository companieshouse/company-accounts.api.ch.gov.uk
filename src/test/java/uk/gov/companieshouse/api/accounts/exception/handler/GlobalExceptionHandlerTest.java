package uk.gov.companieshouse.api.accounts.exception.handler;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.validation.ErrorType;
import uk.gov.companieshouse.api.accounts.validation.LocationType;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private NoHandlerFoundException noHandlerFoundException;

    @Mock
    private HttpMessageNotReadableException httpMessageNotReadableException;

    private HttpHeaders httpHeaders;

    @Mock
    private WebRequest webRequest;

    @Mock
    private InvalidFormatException invalidFormatException;

    @Mock
    private JsonProcessingException jsonProcessingException;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(globalExceptionHandler, "invalidValue", "InvalidValue");
        noHandlerFoundException = new NoHandlerFoundException("","", new HttpHeaders());

        httpHeaders = new HttpHeaders();
    }

    @Test
    @DisplayName("Handle Exception return correct reponse code")
    void testHandleException() {
        ResponseEntity entity = globalExceptionHandler.handleException(new Exception()) ;
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,entity.getStatusCode());
    }

    @Test
    @DisplayName("Handle No handler found Exception")
    void testHandleNoHandlerFoundException() {
        ResponseEntity entity = globalExceptionHandler.handleNoHandlerFoundException(noHandlerFoundException,httpHeaders,HttpStatus.INTERNAL_SERVER_ERROR,webRequest) ;
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,entity.getStatusCode());
    }

    @Test
    @DisplayName("Handle Http Message Not Readable")
    void testHandleHttpMessageNotReadable() {
        when(httpMessageNotReadableException.getCause()).thenReturn(new Throwable());
        ResponseEntity entity = globalExceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException,httpHeaders,HttpStatus.INTERNAL_SERVER_ERROR,webRequest);
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,entity.getStatusCode());
        Errors errors = (Errors) entity.getBody();
        Assert.assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("InvalidValue", "JSON parse exception")));
    }

    @Test
    @DisplayName("Handle Http Message Not Readable Invalid format Exception")
    void testHandleHttpMessageNotReadableInvalidFormatException() {
        when(invalidFormatException.getValue()).thenReturn("value");
        when(invalidFormatException.getLocation()).thenReturn(new JsonLocation("",1L,1,1));
        when(httpMessageNotReadableException.getCause()).thenReturn(invalidFormatException);
        ResponseEntity entity = globalExceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException,httpHeaders,HttpStatus.INTERNAL_SERVER_ERROR,webRequest);
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,entity.getStatusCode());
        Errors errors = (Errors) entity.getBody();
        Assert.assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("InvalidValue", "JSON parse exception:Can not deserialize value of value at line 1 column 1")));
    }

    @Test
    @DisplayName("Handle Http Message Not Readable Json Processing Exception")
    void testHandleHttpMessageNotReadableJsonProcessingException() {
        when(jsonProcessingException.getLocation()).thenReturn(new JsonLocation("",1L,1,1));
        when(httpMessageNotReadableException.getCause()).thenReturn(jsonProcessingException);
        ResponseEntity entity = globalExceptionHandler.handleHttpMessageNotReadable(httpMessageNotReadableException,httpHeaders,HttpStatus.INTERNAL_SERVER_ERROR,webRequest);
        assertNotNull(entity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,entity.getStatusCode());
        Errors errors = (Errors) entity.getBody();
        Assert.assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError("InvalidValue", "JSON parse exception at line 1 column 1")));
    }

    private Error createError(String error, String path) {
        return new  Error(error, path, LocationType.REQUEST_BODY.getValue(),
                ErrorType.VALIDATION.getType());

    }
}
