package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
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
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private Transaction transaction;

    @Mock
    private CompanyAccountEntity companyAccountEntity;

    @Mock
    private SmallFullTransformer smallFullTransformer;

    @Mock
    private SmallFull smallFull;

    @Mock
    private SmallFullEntity smallFullEntity;

    @Mock
    private SmallFullService smallFullService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @InjectMocks
    private SmallFullController smallFullController;

    @Test
    @DisplayName("Tests the successful creation of a smallFull resource")
    public void canCreateSmallFull() throws NoSuchAlgorithmException, DataException {
        ResponseObject<SmallFull> responseObject = new ResponseObject(
                ResponseStatus.CREATED,
                smallFull);
        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                .body(responseObject.getData());
        doReturn(transaction).when(request)
                .getAttribute(AttributeName.TRANSACTION.getValue());
        doReturn(companyAccountEntity).when(request)
                .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());

        doReturn(responseObject).when(smallFullService).create(smallFull, transaction, null,null);
        doReturn(responseEntity).when(apiResponseMapper).map(responseObject.getStatus(),
                responseObject.getData(), responseObject.getValidationErrorData());
        ResponseEntity response = smallFullController.create(smallFull, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(smallFull, response.getBody());
    }

    @Test
    @DisplayName("Tests the successful get of a smallFull resource")
    public void canGetSmallFull() throws NoSuchAlgorithmException {
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
    @DisplayName("Tests the unsuccessful get of a smallFull resource")
    public void getSmallFullFail() throws NoSuchAlgorithmException {
        doReturn(null).when(request)
                .getAttribute(AttributeName.SMALLFULL.getValue());
        when(apiResponseMapper.mapGetResponse(null, request)).thenReturn(ResponseEntity.status(
                HttpServletResponse.SC_NOT_FOUND).build());
        ResponseEntity response = smallFullController.get(request);


        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
    }
}