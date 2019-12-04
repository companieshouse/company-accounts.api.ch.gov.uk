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
import org.springframework.web.bind.WebDataBinder;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.ProfitAndLossService;
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
public class ProfitAndLossControllerTest {

    @Mock
    private ProfitAndLossService profitAndLossService;

    @Mock
    private ApiResponseMapper apiResponseMapper;

    @Mock
    private ProfitAndLoss profitAndLoss;

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
    private CurrentPeriod currentPeriod;

    @Mock
    private PreviousPeriod previousPeriod;

    @Mock
    private WebDataBinder webDataBinder;

    @Mock
    private Map<String, String> links;

    @InjectMocks
    private ProfitAndLossController profitAndLossController;

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountId";
    private static final String PROFIT_AND_LOSS_LINK = "profitAndLossLinks";

    @Test
    @DisplayName("Test the successful creation of profit and loss resource")
    void createProfitAndLossSuccess() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(false);
        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.CREATED, profitAndLoss);
        when(profitAndLossService.create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request))
        .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.CREATED).body(responseObject.getData());
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.create(profitAndLoss, bindingResult,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(profitAndLoss, response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Test the creation of a profit and loss resource when binding result errors are present")
    void createProfitAndLossBindingResultErrors() throws DataException {

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response = profitAndLossController.create(profitAndLoss, bindingResult,
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

        doThrow(new DataException("")).when(profitAndLossService)
                .create(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.create(profitAndLoss, bindingResult,
                COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);

    }

    @Test
    @DisplayName("Tests the successful retrieval of a profit and loss resource")
    void getProfitAndLossSuccess() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.FOUND, profitAndLoss);
        when(profitAndLossService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.FOUND).body(responseObject.getData());
        when(apiResponseMapper.mapGetResponse(responseObject.getData(), request))
                .thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(profitAndLoss, response.getBody());
    }

    @Test
    @DisplayName("Tests the retrieval of a profit and loss resource where the service throws a data exception")
    void getProfitAndLossServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(profitAndLossService).find(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.get(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the successful update of a profit and loss resource for current period")
    void updateProfitAndLossCurrentPeriodSuccess() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.CURRENT_PERIOD;

        when(request.getAttribute(anyString())).thenReturn(currentPeriod).thenReturn(transaction);
        when(currentPeriod.getLinks()).thenReturn(links);
        when(links.get(CurrentPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_AND_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, profitAndLoss);
        when(profitAndLossService.update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }


    @Test
    @DisplayName("Tests the successful update of a profit and loss resource for previous period")
    void updateProfitAndLossPreviousPeriodSuccess() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.PREVIOUS_PERIOD;

        when(request.getAttribute(anyString())).thenReturn(previousPeriod).thenReturn(transaction);
        when(previousPeriod.getLinks()).thenReturn(links);
        when(links.get(PreviousPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_AND_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        ResponseObject responseObject = new ResponseObject(ResponseStatus.UPDATED, profitAndLoss);
        when(profitAndLossService.update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(errorMapper, never()).mapBindingResultErrorsToErrorModel(bindingResult);
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource for current period when the small full link doesn't exist")
    void updateProfitLossNoCurrentPeriodLink() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.CURRENT_PERIOD;
        when(request.getAttribute(anyString())).thenReturn(currentPeriod).thenReturn(transaction);
        when(currentPeriod.getLinks()).thenReturn(links);
        when(links.get(CurrentPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource for previous period when the small full link doesn't exist")
    void updateProfitLossNoPreviousPeriodLink() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.PREVIOUS_PERIOD;
        when(request.getAttribute(anyString())).thenReturn(previousPeriod).thenReturn(transaction);
        when(previousPeriod.getLinks()).thenReturn(links);
        when(links.get(PreviousPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(null);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(bindingResult, never()).hasErrors();
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource for current period when binding result errors are present")
    void updateProfitAndLossCurrentPeriodBindingResultErrors() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.CURRENT_PERIOD;
        when(request.getAttribute(anyString())).thenReturn(currentPeriod).thenReturn(transaction);
        when(currentPeriod.getLinks()).thenReturn(links);
        when(links.get(CurrentPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_AND_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the update of a profit and loss resource for previoud period when binding result errors are present")
    void updateProfitAndLossPreviousPeriodBindingResultErrors() throws DataException {

        AccountingPeriod accountingPeriod = AccountingPeriod.PREVIOUS_PERIOD;
        when(request.getAttribute(anyString())).thenReturn(previousPeriod).thenReturn(transaction);
        when(previousPeriod.getLinks()).thenReturn(links);
        when(links.get(PreviousPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_AND_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(errorMapper.mapBindingResultErrorsToErrorModel(bindingResult)).thenReturn(errors);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody());
    }

    @Test
    @DisplayName("Tests the update of an profit and loss resource where the service throws a data exception")
    void updateProfitLossServiceThrowsDataException() throws DataException {

        AccountingPeriod accountingPeriod  = AccountingPeriod.CURRENT_PERIOD;
        when(request.getAttribute(anyString())).thenReturn(currentPeriod).thenReturn(transaction);
        when(currentPeriod.getLinks()).thenReturn(links);
        when(links.get(CurrentPeriodLinkType.PROFIT_AND_LOSS.getLink()))
                .thenReturn(PROFIT_AND_LOSS_LINK);

        when(bindingResult.hasErrors()).thenReturn(false);

        doThrow(new DataException("")).when(profitAndLossService)
                .update(profitAndLoss, transaction, COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response =
                profitAndLossController.update(profitAndLoss, bindingResult, COMPANY_ACCOUNTS_ID, accountingPeriod, request);

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
        when(profitAndLossService.delete(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(responseObject);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors()))
                .thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the deletion of an profit and loss resource where the service throws a data exception")
    void deleteProfitLossServiceThrowsDataException() throws DataException {

        when(request.getAttribute(AttributeName.TRANSACTION.getValue())).thenReturn(transaction);

        doThrow(new DataException("")).when(profitAndLossService).delete(COMPANY_ACCOUNTS_ID, request);

        ResponseEntity responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(apiResponseMapper.getErrorResponse()).thenReturn(responseEntity);

        ResponseEntity response = profitAndLossController.delete(COMPANY_ACCOUNTS_ID, request);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Tests the deletion of an profit and loss resource where the service throws a data exception")
    void testInitDataBinderSuccess() {

        PeriodConverter converter = new PeriodConverter();
        AccountingPeriod accountingPeriod = AccountingPeriod.CURRENT_PERIOD;
        converter.setAsText("current-period");
        WebDataBinder binder = new WebDataBinder(accountingPeriod, converter.getAsText());
        profitAndLossController.initBinder(binder);
        assertNotNull(binder);
        assertEquals(accountingPeriod.toString(), converter.getAsText());
    }


}