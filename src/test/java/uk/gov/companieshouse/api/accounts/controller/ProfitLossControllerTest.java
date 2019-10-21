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
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ProfitLossService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProfitLossControllerTest {

    @Mock
    private ProfitLossService profitLossService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ProfitLoss profitLoss;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private ErrorMapper errorMapper;

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
    private ProfitLossController profitLossController;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";
    private static final String PROFIT_LOSS_LINK = "profitLossLink";

    @Test
    @DisplayName("Test the successful creation of profit and loss resource")
    void createProfitLossSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, profitLoss);
        when(profitLossService.create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request))
        .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = profitLossController.create(profitLoss, bindingResult,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(profitLoss, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Test the creation of a profit and loss resource when binding result errors are present")
    void createProfitLossBindingResultErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response = profitLossController.create(profitLoss, bindingResult,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the creation of a profit and loss reource where the service throws a data exception")
    void createProfitAndLossServiceThrowsDataException() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(profitLossService)
                .create(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitLossController.create(profitLoss, bindingResult,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);

    }

    @Test
    @DisplayName("Tests the successful retrieval of a profit and loss resource")
    void getProfitLossSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, profitLoss);
        when(profitLossService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = profitLossController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(profitLoss, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of a profit and loss resource where the service throws a data exception")
    void getProfitLossServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(profitLossService).find(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitLossController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }



    @Test
    @DisplayName("Tests the successful update of a profit and loss resource")
    void updateProfitLossSuccess() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, profitLoss);
        when(profitLossService.update(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                profitLossController.update(profitLoss, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource when the small full link doesn't exist")
    void updateProfitLossNoSmallFullLink() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                profitLossController.update(profitLoss, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of an profit and loss resource when binding result errors are present")
    void updateProfitLossBindingResultErrors() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                profitLossController.update(profitLoss, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the update of an profit and loss resource where the service throws a data exception")
    void updateProfitLossServiceThrowsDataException() throws DataException {

        when(request.getAttribute(anyString())).thenReturn(smallFull).thenReturn(transaction);
        when(smallFull.getLinks()).thenReturn(links);
        when(links.get(SmallFullLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(profitLossService)
                .update(profitLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                profitLossController.update(profitLoss, bindingResult, COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the successful deletion of a profit and loss resource")
    void deleteProfitLossSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED);
        when(profitLossService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = profitLossController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the deletion of an profit and loss resource where the service throws a data exception")
    void deleteProfitLossServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(profitLossService).delete(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitLossController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

}
