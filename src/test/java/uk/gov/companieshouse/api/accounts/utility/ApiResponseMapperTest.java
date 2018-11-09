package uk.gov.companieshouse.api.accounts.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import uk.gov.companieshouse.api.accounts.exception.PatchException;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ApiResponseMapperTest {

    @Mock
    RestObject restObject;

    @Mock
    Errors validationErrorData;

    @Mock
    PatchException patchException;

    @Mock
    IllegalArgumentException illegalArgumentException;

    @Mock
    HttpServletRequest request;

    @InjectMocks
    private ApiResponseMapper apiResponseMapper;

    @BeforeEach
    public void setUp() {

    }

    @Test
    @DisplayName("Tests create response")
    void canMapCreateResponse() {
        ResponseEntity responseEntity = apiResponseMapper.map(ResponseStatus.CREATED, restObject, validationErrorData);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(restObject, responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests conflict response")
    void canMapConflictResponse() {
        ResponseEntity responseEntity = apiResponseMapper.map(ResponseStatus.DUPLICATE_KEY_ERROR, restObject, validationErrorData);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests default response")
    void canMapDefaultResponse() {
        ResponseEntity responseEntity = apiResponseMapper.map(ResponseStatus.NOT_FOUND, restObject, validationErrorData);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests exception response")
    void canMapException() {
        ResponseEntity responseEntity = apiResponseMapper.map(patchException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests exception response default")
    void canMapExceptionDefault() {
        ResponseEntity responseEntity = apiResponseMapper.map(illegalArgumentException);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests get mapping response")
    void canMapGetResponseSuccess() {
        ResponseEntity responseEntity = apiResponseMapper.mapGetResponse(restObject, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    @DisplayName("Tests get mapping response")
    void canMapGetResponseFail() {
        ResponseEntity responseEntity = apiResponseMapper.mapGetResponse(null, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}